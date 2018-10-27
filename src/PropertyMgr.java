import java.io.IOException;
import java.util.Properties;
/**
 * 专门创建这个类来管理配置文件，用到了单例模式，singleton
 * @author Administrator
 *
 */
public class PropertyMgr {
	//因为只需要从硬盘load一次文件，所以设置成静态的。
	static Properties props = new Properties();	//用到辅助类，property，来调用配置文件中的内容
	static{
		try {
			props.load(PropertyMgr.class.getClassLoader().getResourceAsStream("config/tank.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private PropertyMgr(){
		//构造方法设置成私有的，别人就不能创建这个的对象了
	}
	
	public static String getProperty(String key){	//就用这个方法来拿到配置文件中的数
		return props.getProperty(key);
	}

}
