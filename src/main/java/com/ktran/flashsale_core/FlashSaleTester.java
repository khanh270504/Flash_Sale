package com.ktran.flashsale_core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleTester {


    private static final String API_URL = "http://localhost:8080/api/orders";
    private static final long PRODUCT_ID = 6;
    private static final int NUM_USERS = 4;
    private static final int LOOP_COUNT = 100;
    private static final int QUANTITY_PER_ORDER = 5;
    // ---------------------

    public static void main(String[] args) throws InterruptedException {
        // Tạo hồ chứa đúng 4 luồng
        ExecutorService executor = Executors.newFixedThreadPool(NUM_USERS);
        HttpClient client = HttpClient.newHttpClient();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger totalItemsSold = new AtomicInteger(0); // Đếm tổng số hàng bán được

        System.out.println(" BẮT ĐẦU TEST: ");
        System.out.println(" Sản phẩm ID: " + PRODUCT_ID);
        System.out.println(" Số lượng mua mỗi đơn: " + QUANTITY_PER_ORDER);
        System.out.println(" Tổng request dự kiến: " + (NUM_USERS * LOOP_COUNT));
        System.out.println("-----------------------------------------");

        long startTime = System.currentTimeMillis();


        for (int i = 1; i <= NUM_USERS; i++) {
            int userId = i;
            executor.submit(() -> {

                for (int j = 0; j < LOOP_COUNT; j++) {
                    try {
                        String jsonBody = """
                            {
                                "userId": %d,
                                "paymentMethod": "COD",
                                "items": [
                                    { "productId": %d, "quantity": %d }
                                ]
                            }
                        """.formatted(userId, PRODUCT_ID, QUANTITY_PER_ORDER);

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200 || response.statusCode() == 201) {
                            successCount.incrementAndGet();
                            totalItemsSold.addAndGet(QUANTITY_PER_ORDER); // Cộng thêm 5
                            System.out.println("✅ User " + userId + ": Mua được " + QUANTITY_PER_ORDER + " cái! (Lần " + (j+1) + ")");
                        } else {
                            failCount.incrementAndGet();
                            System.out.println("❌ User " + userId + " - Lỗi: " + response.statusCode() + " -> " + response.body());
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        if (finished) {
            System.out.println("\n-----------------------------------------");
            System.out.println(" KẾT QUẢ CUỐI CÙNG:");
            System.out.println("⏱ Thời gian: " + (endTime - startTime) + "ms");
            System.out.println(" Số đơn thành công: " + successCount.get());
            System.out.println(" TỔNG HÀNG ĐÃ BÁN: " + totalItemsSold.get());
            System.out.println(" Số đơn bị từ chối: " + failCount.get());
            System.out.println("-----------------------------------------");
        }
    }
}