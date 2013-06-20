/*
 * Copyright 2013 Decebal Suiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
 * the License. You may obtain a copy of the License in the LICENSE file, or at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ro.fortsoft.log2j.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import ro.fortsoft.log2j.RegexEntity;
import ro.fortsoft.log2j.RegexField;

/**
 * @author Decebal Suiu
 */
@RegexEntity(pattern = "PATTERN")
public class Download {

	// [webapp 2008/10/06 16:12:16] - 192.168.12.124, /download/next-reports-setup-1.7-jre.exe, f13dfc7fe609480297a0b15d611676b4	
	public static final String PATTERN = "\\[webapp\\s"
		+ "(20[0-1][0-9]/\\d{2}/\\d{2})" // date
		+ "\\s"
		+ "(\\d{2}:\\d{2}:\\d{2})" // time
		+ "\\]\\s-\\s<\\$>\\s"
		+ "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})" // ip
		+ ",\\s/download/"
		+ "([^,]*)" // file 
		+ ".*";

	private static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy"); // only used in toString()
	
	@RegexField(group = 1, converter = MyDateConverter.class)
	private Date date;
	
	@RegexField(group = 2)
	private String time;
	
	@RegexField(group = 3)
	private String ip;
	
	@RegexField(group = 4)
	private String file;
	
	// other properties
	private String session;
	private String country;
	private String city;
	private String agent;
	private String referer;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("date = " + format.format(date));
		sb.append(",");
		sb.append("time = " + time);
		sb.append(",");
		sb.append("ip = " + ip);
		sb.append(",");
		sb.append("file = " + file);
		
		return sb.toString();
	}
	
}
