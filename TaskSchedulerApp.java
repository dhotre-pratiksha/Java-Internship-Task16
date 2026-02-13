import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskSchedulerApp {

    public static void main(String[] args) {

        System.out.println("=*=*=*=*=*= Executor Framework Task Scheduler =*=*=*=*=*=");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Future<String>> results = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            int taskId = i;

            Callable<String> task = () -> {
                System.out.println("Task " + taskId +
                        " executed by " + Thread.currentThread().getName());

                Thread.sleep(1000);

                if (taskId == 4) {
                    throw new RuntimeException("Error in Task " + taskId);
                }

                return "Task " + taskId + " completed successfully";
            };

            results.add(executor.submit(task));
        }

        for (Future<String> future : results) {
            try {
                System.out.println(future.get());
            } catch (ExecutionException e) {
                System.out.println("Task failed : " + e.getCause().getMessage());
            } catch (InterruptedException e) {
                System.out.println("Task interrupted");
            }
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("\nExecutor shutdown completed.");

        compareWithTraditionalThreads();
    }

    private static void compareWithTraditionalThreads() {

        System.out.println("\n=*=*=*=*=*= Performance Comparison =*=*=*=*=*=");

        long startThreads = System.currentTimeMillis();

        List<Thread> threadList = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            });
            threadList.add(t);
            t.start();
        }

        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }

        long endThreads = System.currentTimeMillis();
        System.out.println("Traditional Threads Time : " +
                (endThreads - startThreads) + " ms");

        long startExecutor = System.currentTimeMillis();

        ExecutorService exec = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 6; i++) {
            exec.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            });
        }
        exec.shutdown();
        try {
            exec.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ignored) {}

        long endExecutor = System.currentTimeMillis();
        System.out.println("Executor Framework Time : " +
                (endExecutor - startExecutor) + " ms");
    }
}

