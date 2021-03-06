package com.azias.openaw.mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azias.openaw.Assets;
import com.azias.openaw.Utils;
import com.azias.openaw.mod.ModInfo.EnumModType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ModLoader extends Object {
	private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
	private Set<Class<?>> modClasses; 
	private ModInfo[] mods;
	private String language;
	
	/** 0-PreAssetsLoad / 1-Assets / 2-PostAssetsLoad */
	private int mainProgess = 0;
	private int subProgress = 0;
	
	@Deprecated
	public ModLoader(String mods) {
		this(mods, "english");
		logger.warn("A deprecated constructor was used for ModLoader!");
	}
	
	public ModLoader(String mods, String language) {
		logger.debug("Initializing ModLoader with \"{}\" as argument", mods);
		this.language = language;
		
		//I was to lazy to find good variables names.
		String[] a = mods.split(";");
		ModInfo[] b = new ModInfo[a.length];
		
		if(a.length<=0) {
			logger.error("The mod list is empty !");
			System.exit(200);
		}
		
		Gson gson = new Gson();
		
		for(int i=0; i<a.length; i++) {
			if(new File("./mods/"+a[i]).exists()) {
				if(!new File("./mods/"+a[i]+"/modinfo.json").exists()) {
					logger.error("Unable to find the \"modinfo.json\" in \"mods/{}/\" !", a[i]);
					System.exit(202);
				}
				logger.debug("Found and loading the modinfo.json for {}...", a[i]);
				try {
					b[i] = gson.fromJson(Utils.fileToString("./mods/"+a[i]+"/modinfo.json"), ModInfo.class);
				} catch (JsonSyntaxException e) {
					logger.error("Syntax error in \"modinfo.json\" from \"{}\"", a[i]);
					e.printStackTrace();
					System.exit(203);
				} catch (IOException e) {
					logger.error("IO error while loading \"modinfo.json\" from \"{}\"", a[i]);
					e.printStackTrace();
					System.exit(204);
				}
				logger.debug("Loaded {}", a[i]);
			} else {
				logger.error("Unable to find the \"{}\" folder in \"mods/\" !", a[i]);
				System.exit(201);
			}
		}
		this.mods = b;
	}
	
	/**
	 * Loads the assets index files for later.
	 */
	public void preload() {
		logger.info("Preloading ModLoader with {} mod(s)", this.mods.length);
		
		logger.debug("Indexing mod classes");
		this.modClasses = new Reflections("").getTypesAnnotatedWith(Mod.class);
		
		logger.debug("Initializing AssetManager and other stuff");
		Assets.assetsManager = new AssetManager();
		Assets.assetsIndex = new Hashtable<String, String>();
		Assets.lang = new Hashtable<String, String>();
		
		JsonParser parser = new JsonParser();
		for(ModInfo mod : this.mods) {
			logger.debug("Preloading {} assets.", mod.name);
			List<EnumModType> modTypes = Arrays.asList(mod.getModTypes());

			if(modTypes.contains(ModInfo.EnumModType.ALL) || modTypes.contains(ModInfo.EnumModType.TEXTURE)) {
				File indexFile = new File("./mods/"+mod.id+"/gfx/index.json");
				if(indexFile.exists()) {
					try {
						JsonObject jObj = (JsonObject)parser.parse(Utils.fileToString(indexFile.getAbsolutePath()));
						for(Entry<String, JsonElement> e : jObj.entrySet()) {
							Assets.assetsIndex.put("gfx."+e.getKey(), "./mods/"+mod.id+"/gfx/"+e.getValue().getAsString());
						}
					} catch (JsonSyntaxException e) {
						logger.error("Syntax error in the \"gfx/{}\" file for \"{}\"", indexFile.getName(), mod.name);
						e.printStackTrace();
						System.exit(207);
					} catch (IOException e) {
						logger.error("IO error while loading \"gfx/{}\" from \"{}\"", indexFile.getName(), mod.name);
						e.printStackTrace();
						System.exit(206);
					}
				} else {
					logger.error("Unable to find the \"gfx/{}\" file for \"{}\"", indexFile.getName(), mod.name);
					System.exit(205);
				}
			}
			if(modTypes.contains(ModInfo.EnumModType.ALL) || modTypes.contains(ModInfo.EnumModType.AUDIO)) {
				File indexFile = new File("./mods/"+mod.id+"/sfx/index.json");
				if(indexFile.exists()) {
					try {
						JsonObject jObj = (JsonObject)parser.parse(Utils.fileToString(indexFile.getAbsolutePath()));
						for(Entry<String, JsonElement> e : jObj.entrySet()) {
							Assets.assetsIndex.put("sfx."+e.getKey(), "./mods/"+mod.id+"/sfx/"+e.getValue().getAsString());
						}
					} catch (JsonSyntaxException e) {
						logger.error("Syntax error in the \"sfx/{}\" file for \"{}\"", indexFile.getName(), mod.name);
						e.printStackTrace();
						System.exit(210);
					} catch (IOException e) {
						logger.error("IO error while loading \"sfx/{}\" from \"{}\"", indexFile.getName(), mod.name);
						e.printStackTrace();
						System.exit(209);
					}
				} else {
					logger.error("Unable to find the \"sfx/{}\" file for \"{}\"", indexFile.getName(), mod.name);
					System.exit(208);
				}
			}
			if(modTypes.contains(ModInfo.EnumModType.ALL) || modTypes.contains(ModInfo.EnumModType.LANG)) {
				File englishFile = new File("./mods/"+mod.id+"/lang/english.json");
				if(englishFile.exists()) {
					try {
						JsonObject jObj = (JsonObject)parser.parse(Utils.fileToString(englishFile.getAbsolutePath()));
						for(Entry<String, JsonElement> e : jObj.entrySet()) {
							Assets.lang.put(e.getKey(), e.getValue().getAsString());
						}
					} catch (JsonSyntaxException e) {
						logger.error("Syntax error in the \"lang/{}\" file for \"{}\"", englishFile.getName(), mod.name);
						e.printStackTrace();
						System.exit(214);
					} catch (IOException e) {
						logger.error("IO error while loading \"lang/{}\" from \"{}\"", englishFile.getName(), mod.name);
						e.printStackTrace();
						System.exit(215);
					}
				} else {
					logger.warn("Unable to find the english lang file for {} !", mod.name);
				}
				//TODO: Add other languages support
			}
			if(modTypes.contains(ModInfo.EnumModType.ALL) || modTypes.contains(ModInfo.EnumModType.MAP)) {
				//Will be used later
			}
			if(modTypes.contains(ModInfo.EnumModType.ALL) || modTypes.contains(ModInfo.EnumModType.CODE)) {
				if(this.getModClass(mod.id)==null) {
					logger.error("Unable to find the main Class for \"{}\"", mod.name);
					System.exit(211);
				}
			}
		}
		
		//TODO: Improve logging here.
		logger.debug("Processing assets entries");
		logger.debug("Index pre-size: {}", Assets.assetsIndex.size());
		Iterator<Entry<String, String>> it = Assets.assetsIndex.entrySet().iterator();
	    while(it.hasNext()) {
	        HashMap.Entry entry = (HashMap.Entry)it.next();
	        if(entry.getKey().toString().startsWith("gfx.")) {
	        	Assets.assetsManager.load(entry.getValue().toString(), Texture.class);
	        } else if(entry.getKey().toString().startsWith("sfx.")) {
	        	
	        } else {
	        	logger.warn("Unable to process this entry: {}", entry.getKey().toString());
	        }
	        //it.remove(); Fucks up the original map
	    };
	    logger.debug("Index post-size: {}", Assets.assetsIndex.size());
	}
	
	public boolean update() {
		switch(this.mainProgess) {
		case 0:
			List<EnumModType> modTypes = Arrays.asList(this.mods[this.subProgress].getModTypes());
			if(!modTypes.contains(ModInfo.EnumModType.CODE) && !modTypes.contains(ModInfo.EnumModType.ALL)) {
				logger.debug("Skipping \"preLoad()\" for {} - {}", this.mods[this.subProgress].id, this.mods[this.subProgress].name);
				if(this.subProgress+1 >= this.mods.length){
					this.subProgress = 0;
					this.mainProgess++;
				} else {
					this.subProgress++;
				}
				break;
			}
			Class<?> modClass = this.getModClass(this.mods[this.subProgress].id);
			if(modClass!=null) {
				logger.debug("Calling preLoad for {} - {}", this.mods[this.subProgress].id, this.mods[this.subProgress].name);
				boolean hasExecutedfunction = false;
				for(Method m : modClass.getMethods()) {
					if(m.getName().equals("preLoad")) {
						try {
							m.invoke(modClass.newInstance());
							hasExecutedfunction = true;
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
							logger.error("An error occured while executing the \"preLoad\" function for \"{}\"", this.mods[this.subProgress].name);
							e.printStackTrace();
							System.exit(212);
						}
					}
				}
				if(!hasExecutedfunction) {
					logger.warn("The preLoad function for {} wasn't executed", this.mods[this.subProgress].name);
				}
				if(this.subProgress+1 >= this.mods.length){
					this.subProgress = 0;
					this.mainProgess++;
				} else {
					this.subProgress++;
				}
			} else {
				logger.error("Unable to find the main Class for \"{}\"", this.mods[this.subProgress].name);
				System.exit(211);
			}
			break;
		case 1:
			if(Assets.assetsManager.update()) {
				this.mainProgess++;
			}
			break;
		case 2:
			return true;
			//break;
		}
		return false;
	}
	
	private Class<?> getModClass(String modId) {
		for(Class<?> c : this.modClasses) {
			if(Strings.isNullOrEmpty(c.getAnnotation(Mod.class).id())) {
				continue;
			}
			if(c.getAnnotation(Mod.class).id().equals(modId)) {
				return c;
			}
		}
		return null;
	}
	
	public String getVerbalStep() {
		if(this.mainProgess==0) {
			return "";
		}
		return null;
	}
	
	//Doesn't do shit
	public float getProgress() {
		if(this.mainProgess==0) {
			return (1/3)*(this.subProgress/this.modClasses.size());
		} else if(this.mainProgess==1) {
			return (1/3)+(1/3)*Assets.assetsManager.getProgress();
		} else {
			return this.mainProgess*(1/3)+(1/3)*(this.subProgress/this.modClasses.size());
		}
	}
	
	public boolean loadEverything(boolean doPreload) {
		if(doPreload) {
			this.preload();
		}
		while(!this.update()) {
			
		}
		return true;
	}
	
	@Deprecated
	public void loadModCode() {
		//System.out.println("Loading mod classes..");
		//this.loadModClasses();
		//System.out.println("Done");
		
		//System.out.println("Pre-loading mods... - "+this.mods.length);
		for(int i=0; i<this.mods.length; i++) {
			//System.out.println("Pre-loading "+this.mods[i]+"...");
			//System.out.println("Pre-loading "+this.mods[i].id+"...");
			
			List<EnumModType> modTypes = Arrays.asList(this.mods[i].getModTypes());
			
			if(modTypes.contains(ModInfo.EnumModType.ALL) || modTypes.contains(ModInfo.EnumModType.CODE)) {
				this.executeModClassMethod(this.mods[i].getId(), "preLoad");
			}
		}
		//System.out.println("Done");
	}
	
	@Deprecated
	private void executeModClassMethod(String modId, String methodName) {
		//System.out.println("Attempting to preload "+modId);
		mainloop:
		for(Class<?> c : this.modClasses) {
			if(Strings.isNullOrEmpty(c.getAnnotation(Mod.class).id())) {
				//System.out.println("Mod class "+c.getName()+" doesn't have an id !");
				continue;
			}
			if(!c.getAnnotation(Mod.class).id().equals(modId)) {
				//System.out.println(modId+" != "+c.getAnnotation(Mod.class).id());
				continue;
			}
			
			for(Method m : c.getMethods()) {
				if(m.getName().equals(methodName)) {
					try {
						m.invoke(c.newInstance());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
						e.printStackTrace();
					}
					//I fell so dirty for doing this.
					break mainloop;
				}
			}
		}
	}
}
