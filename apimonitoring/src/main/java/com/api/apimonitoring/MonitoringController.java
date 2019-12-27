package com.api.apimonitoring;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.util.JSON;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class MonitoringController {

	@Autowired
	IMonitoringRepository monitoringRepository;
	
	@Autowired
	MonitoringBusinessLogic monitoringBL;
	
	
	@PostMapping("/add")
	public Monitors createMonitor(@RequestBody Monitors monitors  ) {
		//System.out.println(monitors.getJsonBody());
		//Object a = monitors.getJsonBody();
		//a.replace(/\n/g,"");
		
	   String a = monitors.getJsonBody().toString().replaceAll("\\s+", "");
	   monitors.setJsonBody(JSON.parse(a));
		return monitoringRepository.save(monitors);
	}
	
//	@PostMapping("/save")
//	public void save(@RequestBody Monitors monitors) {
//		monitoringRepository.save(monitors);
//	}
	
	
	@GetMapping("/findall")
	public Stream<Monitors> findall() {
	 monitoringRepository.findAll().stream().forEach(System.out::println);
		return monitoringRepository.findAll().stream();
	}
	
	@GetMapping("/findone/{id}")
	public Optional<Monitors> findone(@PathVariable("id") String id) {
		System.out.println(monitoringRepository.findById(id));
		return monitoringRepository.findById(id);
	}
	
	@GetMapping("/findurl/{id}")
	public Monitors findUrl(@PathVariable("id") String id){
		System.out.println(monitoringRepository.findOneMonitor(id).getTime());
		Monitors a = monitoringRepository.findOneMonitor(id);
		Long time = a.getTime();
		System.out.println(time);
		String url = a.getUrl();
		System.out.println(url);
		Object jsonBody = a.getJsonBody();
		System.out.println(jsonBody);
		
		List<Headers> headers = a.getHeaders();
		for(Headers header : headers) {
			System.out.println(header.getHeaderKey() + "  key");
			System.out.println(header.getHeaderValue() + "   value");
		}
		
		System.out.println(a.isExecuting());
		a.setExecuting(true);
		monitoringRepository.save(a);
		
		
		return monitoringRepository.findOneMonitor(id);
	}
	
	
	// starting url runs
	@GetMapping("/runUrls/{id}")
	public Monitors runUrls(@PathVariable String id) {
		Monitors m = monitoringRepository.findOneMonitor(id);
		m.setExecuting(true);
		monitoringRepository.save(m);
		monitoringBL.start(true, id);
		return monitoringRepository.findById(id).get();
		
	}
	
	// stopping urls
	  @GetMapping("/stop/{id}")
    public Monitors stopUrls(@PathVariable String id) {
	  Monitors m = monitoringRepository.findOneMonitor(id);
	  m.setExecuting(false);
	  monitoringRepository.save(m);
        return monitoringRepository.findById(id).get();
    }
	
	  
	  @GetMapping("/findOne/{id}")
		public Monitors getOneMonitor(@PathVariable String id ) {
			return monitoringRepository.findById(id).get();
		}
	
		@GetMapping("/get")
		public List<Monitors> getAllMonitors() throws InterruptedException {
			return monitoringRepository.findAll();
		}
		
		
		@PutMapping(path="/update")
		public String updateMonitor(@RequestBody Monitors monitors)
		{
			monitoringRepository.save(monitors);
			System.out.println("update success");
			return "updated";
		}
		
		@DeleteMapping(path="/delete/{id}")
		public  ResponseEntity<Object> delete(@PathVariable("id") String id) {
			System.out.println("deleting");
			//monitoringRepository.deleteById(id);
//		    Monitors m = monitoringRepository.findOneMonitor(id);
//		    monitoringRepository.delete(m);
//			System.out.println("deleted");
			//return "deleted";
			
			monitoringRepository.deleteBy(id);
			
			return ResponseEntity.noContent().build();
		}
	
		
	
		@GetMapping(path="/getTotalCounts")
		public long getTotalCounts() {
			return monitoringRepository.count();
		}
	
		
//		@GetMapping(path="/getSuccessCounts")
//		public long getSuccessCounts() {
//			return monitoringRepository.findTotalSuccessCount();
//		}
		
		@GetMapping(path="/start/{id}")
		public void start(@PathVariable String id) {
			monitoringBL.start(true,id);
		}
		
		@GetMapping(path="/stopping/{id}")
		public void stop(@PathVariable String id) {
			 Monitors m = monitoringRepository.findOneMonitor(id);
			  m.setExecuting(false);
			  monitoringRepository.save(m);
		}
		
		
}
