package cz.agents.alite.configurator;

import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapConfiguration implements ConfigurationInterface {

	Map<String, Object> values;

	public MapConfiguration(Map<String, Object> values) {
		this.values = values;
	}
	
	/**
	 * Var-args constructor
	 * @param args Arguments correspond to map entries - key,value,key,value,.. where key must be a String
	 */
	public MapConfiguration(Object ... args) {
		values = new HashMap<String, Object>();
		
		for(int i = 0; i + 1 < args.length; i+=2){
			if(args[i] instanceof String){
				values.put((String)args[i], args[i+1]);
			}else{
				throw new IllegalArgumentException("Keys must be Strings!");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getValue(String key, T defaultValue) {
		Object value = values.get(key);
		if (value != null && value.getClass().equals(defaultValue.getClass())) {
			return (T) values.get(key);
		} else {
			return defaultValue;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getValue(String key) {
		return (T) values.get(key);
	}

	@Override
	public void writeTo(Writer writer) {
		throw new UnsupportedOperationException("Not implemented yet!!!");
	}

	@Override
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return getValue(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public double getDouble(String key) {
		return getValue(key);
	}

	@Override
	public double getDouble(String key, double defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public float getFloat(String key) {
		return getValue(key);
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public int getInt(String key) {
		return getValue(key);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public Object getObject(String key) {
		return getValue(key);
	}

	@Override
	public Object getObject(String key, Object defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public List<String> getKeyList() {
		return new ArrayList<String>(values.keySet());
	}

	@Override
	public String getString(String key) {
		return getValue(key);
	}

	@Override
	public String getString(String key, String defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public BigDecimal getBigDecimal(String key) {
		return getValue(key);
	}

	@Override
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public <TKey, TValue> Map<TKey, TValue> getMap(String key) {
		return getValue(key);
	}

	@Override
	public <TKey, TValue> Map<TKey, TValue> getMap(String key,
			Map<TKey, TValue> defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public <TValue> List<TValue> getList(String key) {
		return getValue(key);
	}

	@Override
	public <TValue> List<TValue> getList(String key, List<TValue> defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public ConfigurationInterface getSubTree(String prefix) {
		throw new UnsupportedOperationException("Not implemented yet!!!");
	}

}
