import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class MissileDeadMsg implements Msg {
	int msgType = Msg.MISSILE_DEAD_MSG;
	
	int tankID;
	int id;
	
	TankClient tc;
	
	public MissileDeadMsg(int tankID, int id){
		this.tankID = tankID;
		this.id = id;
	}
	
	
	public MissileDeadMsg(TankClient tc){
		this.tc = tc;
	}
	
	
	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tankID);
			dos.writeInt(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void parse(DataInputStream dis) {
		try {
			int tankID = dis.readInt();
			if(tc.myTank.id == tankID) {
				return;
			}
			
			int id = dis.readInt();
			
			for(int i=0; i<tc.missiles.size(); i++) {
				Missile m = tc.missiles.get(i);
				if(m.id == id && m.tankID == tankID) {
					m.setLive(false);
					tc.explodes.add(new Explode(m.x, m.y, tc));
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
