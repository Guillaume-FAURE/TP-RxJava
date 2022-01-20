package ibd.rxjava.fortune;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FortuneStreamClient {

	public static void main(String[] args) {
		Observable<FortuneData> fortuneObs = FortuneStream.cfrom("localhost", 10000);
		Observable<FortuneData> slowFortuneObs = FortuneStream.cfrom("localhost", 20000);
		//partie1(fortuneObs);
		//partie2(fortuneObs);
		partie3(fortuneObs,slowFortuneObs);
		
		try {
			System.in.read();
			System.out.println("exiting");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static void partie1(Observable<FortuneData> fortuneObs) {
		Disposable disposable = fortuneObs
				.filter(fd->fd.getDate().getSeconds()%2==0)
				.filter(fd->fd.getText().split("\n").length<=3)
				.subscribe(fd -> System.out.println("Fortune : \n" + fd),err -> System.out.println("error:" + err));
		disposable.dispose();
	}
	private static void partie2(Observable<FortuneData> fortuneObs) {
		/*Disposable disposable = fortuneObs
				.map(fd->fd.getText())
				.map(str->str.length())
				.subscribe(size->System.out.println("Size : " + size),err-> System.out.println("error:" + err));*/
		/*List<String> words = Arrays.asList("is","the");
		Disposable disposable = fortuneObs
				.map(fd->Arrays.asList(fd.getText().split(" ")))
				.filter(word -> {
					for(String w:word) {
				null		if (words.contains(w)) {
							return true;
						}
					}
					return false;
				})
				.forEach(System.out::println);*/
		
		Map<String, Integer> frequencies = new TreeMap<String, Integer>(); 
		
		Disposable disposable = fortuneObs
				.flatMap(fd -> Observable.fromArray(fd.getText().split("\\s+")))
				.doOnNext(word -> {
					String s = word.trim().toLowerCase();
					if (s.length() != 0) {
							frequencies.put(s, frequencies.getOrDefault(s, 0)+1);
					}
				})	
				.forEach(w ->{
					if (frequencies.size()%20==0) {
						System.out.println("current frequencies:");
						System.out.println(frequencies);
					}
				});
	}
	private static void partie3(Observable<FortuneData> fortuneObs,Observable<FortuneData> slowFortuneObs) {
		/*System.out.println("Start");
		Disposable disposable = fortuneObs
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe(fd -> System.out.println(fd));
		
		Observable.timer(20, TimeUnit.SECONDS)
			.observeOn(Schedulers.io())
			.subscribe(l -> {
				System.out.println("Stop fortune stream");
				disposable.dispose();
			});
			
		for (int i=0;i<100;i++) {
			int time = 5*i;
			Observable.timer(time, TimeUnit.SECONDS)
				.observeOn(Schedulers.io())
				.subscribe(l -> System.out.println("After "+time+" secondes"));
		}*/
		Observable textObs = fortuneObs.map(fd -> fd.getText().toLowerCase());
		Observable slowTextObs = slowFortuneObs.map(fd -> fd.getText().toUpperCase());
		
		Disposable disposable = Observable.merge(textObs, slowTextObs)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.doOnNext(System.out::println)
				.subscribe();

	}
}
