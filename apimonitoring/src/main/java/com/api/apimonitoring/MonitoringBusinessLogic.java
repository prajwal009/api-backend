package com.api.apimonitoring;




import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

@EnableAsync
@EnableScheduling
@Service
public class MonitoringBusinessLogic {
	
	Monitors monitors;
	@Autowired
	IMonitoringRepository monitoringRepository;   
	    
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public void start(boolean bool, String id) {
		
//		String url = monitoringRepository.findUrl(id);
//		String methodType = monitoringRepository.findMethodType(id);
//		Long time = monitoringRepository.findTime(id);
//		String jsonBody = monitoringRepository.findJsonBody(id);
//		String headerName = monitoringRepository.findHeaderName(id);
//		String headerValue = monitoringRepository.findHeaderValue(id);
		
		
		Monitors monitorOne = monitoringRepository.findOneMonitor(id);
		String url =monitorOne.getUrl();
		String methodType = monitorOne.getMethodType();
		Long time = monitorOne.getTime();
		Object jsonBody = monitorOne.getJsonBody();
		
		List<Headers> headers = monitorOne.getHeaders();
		
		Runnable runne = new Runnable() {

			@SuppressWarnings({ "deprecation" })
			@Override
			public void run() {
				
				System.out.println("started run");
				
					//int stopped = monitoringRepository.findIsExecuting(id);
					boolean stopped = monitorOne.isExecuting();
					System.out.println(stopped + " stopped value");
					
					Monitors m1 = monitoringRepository.findOneMonitor(id);
					boolean stopp = m1.isExecuting();
					
					if(stopp == true) {					
						try {
							System.out.println("entered");
							//monitoringRepository.updateIsExecuting(id, false);
							int statusCode = sendHttpUnirest(url,methodType,headers,jsonBody);
							System.out.println(statusCode+"  "+id);
							//monitoringRepository.updateStatusCode(id, statusCode);
							monitorOne.setStatusCode(statusCode);
							monitoringRepository.save(monitorOne);
							
							//float successCount=monitoringRepository.findSuccessCount(id);
							float successCount = monitorOne.getSuccessCount();
							
							//float totalRuns = monitoringRepository.findTotalRuns(id);
							float totalRuns = monitorOne.getTotalRuns();
							if(statusCode==200) {
								successCount++;
								monitorOne.setSuccessCount(successCount);
							}
							totalRuns++;
							monitorOne.setTotalRuns(totalRuns);
							float apdex = calculateApdex(successCount,totalRuns);
							monitorOne.setApdex(apdex);
							//monitoringRepository.updateApdex(id, successCount, totalRuns, apdex);
							System.out.println(monitorOne.isExecuting());
							monitoringRepository.save(monitorOne);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("cought");
							e.printStackTrace();
						}
						
						
						
					}
				
					else {
						System.out.println("stoppping");
						Thread.currentThread().stop();
					}
					
			}
			
		};
		// monitoringRepository.updateIsExecuting(id, true);
		 //monitorOne.setExecuting(true);
		 //monitoringRepository.save(monitorOne);
		 ScheduledFuture<?> runneHandle = 
	    			scheduler.scheduleAtFixedRate(runne, time,time, SECONDS);
		 
		 
			 scheduler.schedule(new Runnable() {
				    @Override
		    		public void run() {
				    
				   
				   	if(bool == true) {
				    		runneHandle.toString();
			    	}
			    	else {
			    		runneHandle.cancel(true);
				    		System.out.println("cancelled");
				 
				    	}
		    		}
		    	}, time, SECONDS); 
		 
		 
			
	}
	

	    
	    //........................unirest start...........................
	    
	    public int sendHttpUnirest(String url,String methodType , List<Headers> headersList , Object jsonBody ) throws Exception
	    {
	        int statuscod = 0 ;
	        Unirest.config().reset();
        	Unirest.config().enableCookieManagement(false);
        	
        	Map<String, String> headers = new TreeMap<String, String>();
        	
        	if(headersList!=null) {
        		for(Headers header : headersList) {
            		
        			//System.out.println(header.getHeaderKey() + "  key");
        			headers.put(header.getHeaderKey(), header.getHeaderValue());
        			//System.out.println(header.getHeaderValue() + "   value");
        		}
        	}
        	
        	
//        	
//            headers.put("Content-Type", "application/json");
//            headers.put("Authorization", "Basic YXM6YXM=");
        	
//	        
	        switch(methodType)
	        {
	            case "GET":
	            	
	                HttpResponse<JsonNode> getResponse=Unirest.
	                get(url).headers(headers)
                   .asJson();
	                    statuscod=getResponse.getStatus();
	                    break;
	            case "POST":
	                HttpResponse<String> postResponse=Unirest.post(url).headers(headers)
	                .body(jsonBody)
	                .asString();
	                statuscod=postResponse.getStatus();
	                break;
	            case "PUT":
	                HttpResponse<String> putResponse=Unirest.put(url).headers(headers)
	                .body(jsonBody)
	                .asString();
	                statuscod=putResponse.getStatus();
	                break;
	            case "DELETE":
	                HttpResponse<String> deleteResponse=Unirest.delete(url).headers(headers)
	                .asString();
	                statuscod=deleteResponse.getStatus();
	                break;   
	        }
	        return statuscod;
	       
	    }
	    
	    
	    
	    //......................unirest end...........................
	    
	    
	    
	    
	    
	    
	    
	   // .............calculating apdex...................
	 public float calculateApdex(float successCount,float totalRuns) {
		 if(totalRuns!=0) {
			 return successCount/totalRuns;
		 }
		 return 0;
	 }
	 
	//.......................................................
	

}