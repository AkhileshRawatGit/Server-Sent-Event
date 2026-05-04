package com.SseExample.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*")
public class SseStatusController {

    private Map<String , SseEmitter>clients=new ConcurrentHashMap<>();


    @GetMapping("/subscribe/{JobId}")
    public SseEmitter subscribe(@PathVariable String JobId){
        SseEmitter sseEmitter=new SseEmitter(0L);
        clients.put(JobId,sseEmitter);
        sseEmitter.onCompletion(() -> clients.remove(JobId));
        sseEmitter.onTimeout(() -> clients.remove(JobId));
        return sseEmitter;
    }

    @GetMapping("/send/{JobId}")
    public String send(@PathVariable String JobId){
        SseEmitter sseEmitter=clients.get(JobId);
        new Thread(() -> {
            try {
                sseEmitter.send("🚀 Process Started");

                for (int i = 1; i <= 5; i++) {
                    Thread.sleep(1000);
                    sseEmitter.send("Step " + i + " completed");
                }

                sseEmitter.send("✅ Process Finished");
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        }).start();

        return "Started";
    }

}
