package com.ccreanga.jersey.example.agent;


import java.util.concurrent.*;


public class RxUtils {
    
    
    public static <T, U> CompletionStage<BiResult<T, U>> join(CompletionStage<T> future1, 
                                                              CompletionStage<U> future2) {
        return future1.thenCombine(future2, (result1, result2) -> new BiResult<>(result1, result2)); 
    }

    
    public static class BiResult<T, U> {
        private final T result1;
        private final U result2;
        
        public BiResult(T result1, U result2) {
            this.result1 = result1;
            this.result2 = result2;
        }
           
        public T getResult1() {
            return result1;
        }
        
        public U getResult2() {
            return result2;
        }
    }
    
    
    public static <T, U, V> CompletionStage<TriResult<T, U, V>> join(CompletionStage<T> future1, 
                                                                     CompletionStage<U> future2,
                                                                     CompletionStage<V> future3) {
        return future1.thenCombine(future2, (result1, result2) -> new BiResult<>(result1, result2))
                      .thenCombine(future3, (biResult, result3) -> new TriResult<>(biResult.getResult1(), 
                                                                                   biResult.getResult2(),
                                                                                   result3)); 
    }
    
    public static class TriResult<T, U, V> {
        private final T result1;
        private final U result2;
        private final V result3;
        
        public TriResult(T result1, U result2, V result3) {
            this.result1 = result1;
            this.result2 = result2;
            this.result3 = result3;
        }
           
        public T getResult1() {
            return result1;
        }
        
        public U getResult2() {
            return result2;
        }
    
        public V getResult3() {
            return result3;
        }
    }    
}



