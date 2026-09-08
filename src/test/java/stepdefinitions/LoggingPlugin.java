package stepdefinitions;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cucumber'ın resmi event sistemini (TestStepStarted / TestStepFinished /
 * TestCaseStarted / TestCaseFinished) kullanarak step adlarını ve sürelerini loglar.
 *
 * Hooks.java'daki reflection + stack trace tabanlı eski yaklaşımın yerini alır:
 * - Scenario#getCurrentStepName() diye bir metod Cucumber 7.x'te YOK, bu yüzden
 *   eski kod her zaman exception yiyip stack trace'e düşüyordu.
 * - Stack trace taraması, debugger-agent / lambda / JIT davranışına göre
 *   yanlış frame'i (hatta metodun kendisini) yakalayabiliyordu -> "Unknown Step"
 *   ya da "getStepNameFromStackTrace" gibi hatalı loglar.
 *
 * Bu plugin, gerçek Gherkin step metnini doğrudan Cucumber'dan alır; asla yanlış
 * isim üretmez.
 *
 * Kayıt (runner sınıfınızda, ör: runners.StudentRunner):
 *
 *   @CucumberOptions(
 *       plugin = {"stepdefinitions.LoggingPlugin", ... diğer pluginleriniz ...}
 *   )
 */
public class LoggingPlugin implements ConcurrentEventListener {

    private static final Logger logger = LogManager.getLogger(LoggingPlugin.class);
    private static final long SLOW_STEP_THRESHOLD_MS = 3000;

    // Paralel çalıştırmalarda thread bazlı karışmayı önlemek için step id -> baslangic zamani
    private final Map<String, Long> stepStartTimes = new ConcurrentHashMap<>();
    // Paralel senaryolarda ayrı sayaç tutmak için thread bazlı sayaç
    private final ThreadLocal<Integer> stepCounter = ThreadLocal.withInitial(() -> 0);
    private final ThreadLocal<Long> testStartTime = new ThreadLocal<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::onTestCaseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::onTestStepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::onTestStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
    }

    private void onTestCaseStarted(TestCaseStarted event) {
        stepCounter.set(0);
        testStartTime.set(System.currentTimeMillis());

        logger.info("═══════════════════════════════════════════════════");
        logger.info("📋 TEST BAŞLIYOR: {}", event.getTestCase().getName());
        logger.info("📌 Tags: {}", event.getTestCase().getTags());
        logger.info("📂 Feature: {}", getFeatureName(event.getTestCase()));
        logger.info("═══════════════════════════════════════════════════");
    }

    private void onTestStepStarted(TestStepStarted event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep)) {
            return; // Hook adımlarını (@Before/@After metodları) loglama, sadece gerçek Gherkin stepleri
        }
        PickleStepTestStep step = (PickleStepTestStep) event.getTestStep();
        int count = stepCounter.get() + 1;
        stepCounter.set(count);

        String stepText = step.getStep().getKeyword() + step.getStep().getText();
        stepStartTimes.put(stepKey(String.valueOf(event.getTestCase().getId()), step), System.currentTimeMillis());

        logger.info("▶️  STEP {}: {}", count, stepText);
    }

    private void onTestStepFinished(TestStepFinished event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep)) {
            return;
        }
        PickleStepTestStep step = (PickleStepTestStep) event.getTestStep();
        String stepText = step.getStep().getKeyword() + step.getStep().getText();
        int count = stepCounter.get();

        Long start = stepStartTimes.remove(stepKey(String.valueOf(event.getTestCase().getId()), step));
        long duration = (start != null) ? System.currentTimeMillis() - start : -1;

        Result result = event.getResult();
        if (result.getStatus() == Status.PASSED) {
            logger.info("✅ STEP {} TAMAMLANDI: {} (Süre: {}ms)", count, stepText, duration);
            if (duration >= SLOW_STEP_THRESHOLD_MS) {
                logger.warn("⚠️  YAVAŞ STEP {}: {} ({}ms)", count, stepText, duration);
            }
        } else {
            logger.error("❌ STEP {} BAŞARISIZ: {} (Süre: {}ms)", count, stepText, duration);
            if (result.getError() != null) {
                logger.error("❌ Hata Mesajı: {}", result.getError().getMessage());
            }
        }
    }

    private void onTestCaseFinished(TestCaseFinished event) {
        Long start = testStartTime.get();
        long duration = (start != null) ? System.currentTimeMillis() - start : -1;

        logger.info("═══════════════════════════════════════════════════");
        if (event.getResult().getStatus() == Status.PASSED) {
            logger.info("✅ TEST BAŞARIYLA TAMAMLANDI: {}", event.getTestCase().getName());
        } else {
            logger.error("❌ TEST BAŞARISIZ: {}", event.getTestCase().getName());
        }
        logger.info("📊 Toplam Test Süresi: {}ms", duration);
        logger.info("📊 Toplam Step Sayısı: {}", stepCounter.get());
        logger.info("═══════════════════════════════════════════════════");

        // ThreadLocal temizliği
        stepCounter.remove();
        testStartTime.remove();
    }

    private String stepKey(String testCaseId, PickleStepTestStep step) {
        return testCaseId + "#" + step.getId();
    }

    private String getFeatureName(TestCase testCase) {
        try {
            String uri = testCase.getUri().toString();
            String fileName = uri.substring(uri.lastIndexOf('/') + 1);
            return fileName.replace(".feature", "");
        } catch (Exception e) {
            return "Unknown Feature";
        }
    }
}