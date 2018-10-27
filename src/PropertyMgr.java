import java.io.IOException;
import java.util.Properties;
/**
 * ר�Ŵ�������������������ļ����õ��˵���ģʽ��singleton
 * @author Administrator
 *
 */
public class PropertyMgr {
	//��Ϊֻ��Ҫ��Ӳ��loadһ���ļ����������óɾ�̬�ġ�
	static Properties props = new Properties();	//�õ������࣬property�������������ļ��е�����
	static{
		try {
			props.load(PropertyMgr.class.getClassLoader().getResourceAsStream("config/tank.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private PropertyMgr(){
		//���췽�����ó�˽�еģ����˾Ͳ��ܴ�������Ķ�����
	}
	
	public static String getProperty(String key){	//��������������õ������ļ��е���
		return props.getProperty(key);
	}

}
