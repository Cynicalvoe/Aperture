package tech.hadenw.aperture;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

public class DRM {
	private int a;
	private Aperture o;
	
	public DRM(){
		o = ((Aperture)Bukkit.getPluginManager().getPlugin("Aperture"));
		a = -1;
		this.newLicensing();
		this.validate();
	}
	
	public void newLicensing() {
		try {
			URL url = new URL(o.getUURL());
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.connect();
			
			try(InputStream is = http.getInputStream()){
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
				String response = "";
				String line;
				while((line = br.readLine()) != null) {
					response+=line;
				}
				
				br.close();
				is.close();
				http.disconnect();
				
				a = Integer.parseInt(response);
			}
		}catch(Exception e) {
			o.getLogger().log(Level.WARNING, "An error occurred when starting up!");
		}
	}
	
	private void validate(){
		if(a==0)
			this.disable();
		if(a==2)
			this.disable();
	}
	
	private void disable(){
		System.out.println(new String(Base64.getDecoder().decode("ICAgICBfICAgICAgICAgICAgICAgICAgICBfICAgICAgICAgICAgICAgICAgDQogICAgLyBcICAgXyBfXyAgIF9fXyBfIF9ffCB8XyBfICAgXyBfIF9fIF9fXyANCiAgIC8gXyBcIHwgJ18gXCAvIF8gXCAnX198IF9ffCB8IHwgfCAnX18vIF8gXA0KICAvIF9fXyBcfCB8XykgfCAgX18vIHwgIHwgfF98IHxffCB8IHwgfCAgX18vDQogL18vICAgXF9cIC5fXy8gXF9fX3xffCAgIFxfX3xcX18sX3xffCAgXF9fX3wNCiAgICAgICAgIHxffCAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIA0KKlBMVUdJTiBTVVNQRU5ERUQqDQoNClRoaXMgY29weSBvZiBBcGVydHVyZSBoYXMgYmVlbiBkZWFjdGl2YXRlZC4NClBvc3NpYmxlIHJlYXNvbnMgaW5jbHVkZToNCjEuIFRvbyBtYW55IGFjdGl2YXRpb25zIChtYXggb2YgNSkNCjIuIFNvZnR3YXJlIHBpcmFjeQ0KMy4gQnJlYWNoIG9mIHRoZSBFVUxBDQpJZiB5b3UgYmVsaWV2ZSB0aGlzIGlzIGEgbWlzdGFrZSwgcGxlYXNlIGNvbnRhY3QNCnRoZSBkZXZlbG9wZXIgb24gU3BpZ290TUMub3JnDQotLS0tLS0tLS0t".getBytes())));
		try{
			File pp = o.getDataFolder().getParentFile();
			File[] aa;
			int bb = (aa = pp.listFiles()).length;
			for(int ii=0; ii<bb; ii++){
				File cc = aa[ii];
				if(cc.getName().endsWith(".jar")){
					PluginDescriptionFile ff = o.getPluginLoader().getPluginDescription(cc);
					if(ff.getName().equalsIgnoreCase("Aperture")){
						FileUtils.forceDelete(cc);
						FileUtils.forceDeleteOnExit(cc);
					}
				}
			}
		}catch(Exception e){}
		Bukkit.getPluginManager().disablePlugin(o);
	}
}
