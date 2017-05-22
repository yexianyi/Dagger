package net.yxy.dagger.nlp.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupTest {

	public static void main(String[] args) throws Exception {
		URL url = new URL("https://www.cloudera.com/documentation/enterprise/latest/topics/impala_datatypes.html");
		Document doc = Jsoup.parse(url, 3 * 1000);

		String text = doc.body().text();

		System.out.println(text); // outputs 1

	}

}
