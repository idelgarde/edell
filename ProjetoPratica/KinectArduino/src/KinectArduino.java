import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.awt.Robot;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import javax.swing.JOptionPane;

import ch.aplu.jaw.NativeHandler;
import ch.aplu.kinect.Kinect;
import ch.aplu.kinect.Point3D;
import ch.aplu.kinect.SkeletonJoint;
import ch.aplu.util.ModelessOptionPane;

public class KinectArduino {
	private final static String Porta = "COM3";

	enum State {
		MOVING_RIGHT, MOVING_LEFT, // Move a mão direita ou esquerda
		WAIT_FOR_START, // Espera pela mão direita para iniciar
		WAIT_FOR_COMMAND, // Aguarda movimento
		OUT_OF_RANGE // Fora de alcance
	};

	private final int speedLimit;
	private final int movDireita;
	private final int movEsquerda;
	String rightKeyCodeStr;
	String leftKeyCodeStr;
	private String dllPath =  Kinect.is64bit() ? "KinectHandler64" : "KinectHandler";
	private int largura = 640;
	private int altura = 480;
	private Point3D[] joints = new Point3D[20];
	private ModelessOptionPane mop;
	private Robot robot;
	private State status = State.OUT_OF_RANGE;
	private static String iconResourcePath = "gifs/kinect.gif";
	long oldTime = 0;
	double xRightOld = 0;
	double xLeftOld = 0;
	boolean isPrimeiro = true;
	
	CommPortIdentifier idPorta = null;
	CommPort commPort = null;
	SerialPort serialPort  = null;
	OutputStream out = null;
	int op = 0;
	boolean escreveD = false;
	boolean escreveE = false;
	
	public KinectArduino() {
		KinectPropriedades props = new KinectPropriedades();
		speedLimit = props.getIntValue("SpeedLimit");
		rightKeyCodeStr = props.getStringValue("RightKeyCode");
		movDireita = props.getKeyCode(rightKeyCodeStr);
		leftKeyCodeStr = props.getStringValue("LeftKeyCode");
		movEsquerda = props.getKeyCode(leftKeyCodeStr);
		ClassLoader loader = getClass().getClassLoader();
		URL iconUrl = loader.getResource(iconResourcePath);
		mop = new ModelessOptionPane(20,20, ""	+ "Inicializando...", iconUrl);
		mop.setTitle("Projeto Kinect-Arduino");

		//abre as portas
		try {
			idPorta = CommPortIdentifier.getPortIdentifier(Porta);
			commPort = idPorta.open(Porta, 2000);

			//inicia o kinect
			Kinect kinect =	new Kinect(dllPath, "Video Frame", 400, 10, largura, altura, NativeHandler.WS_BORDER | NativeHandler.WS_VISIBLE);
			kinect.setWindowScaleFactor(4);
			if (!kinect.isInitialized()) {
				Kinect.delay(1000);
				mop.setText("Kinect não ecnontrado"
						+ "\n- Não conectado?"
						+ "\n- Não está ligado?"
						+ "\n- Já Está funcionando?", false);
				return;
			}
			for (int i = 0; i < 20; i++) {
				joints[i] = new Point3D();
			}

			robot = new Robot();

			exibirStatus("Ninguém ao alcance", true);
			
			if ( !(commPort instanceof SerialPort) ) {
				throw new Exception("commPort não é um SerialPort!");
			}
			
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

			while (true) {
				int skeletonId = kinect.getJoints(joints, 20);  // max 200 ms
				if (skeletonId <= -1) {  // Inválido
					mudarStatus(State.OUT_OF_RANGE);
				} else {  // Válido
					if (status == State.OUT_OF_RANGE) {
						mudarStatus(State.WAIT_FOR_START);
						exibirStatus("Levante a mão para iniciar");
					}

					long time = new Date().getTime();

					if (time - oldTime > 200) {
						oldTime = time;
						int maoDireita = SkeletonJoint.HAND_RIGHT.ordinal();
						int maoEsquerda = SkeletonJoint.HAND_LEFT.ordinal();
						double xRight = joints[maoDireita].x;
						double yRight = joints[maoDireita].y;
						double xLeft = joints[maoEsquerda].x;
						double yLeft = joints[maoEsquerda].y;

						if (status == State.WAIT_FOR_COMMAND && yLeft < 500) { // Para
							status = State.WAIT_FOR_START;
							exibirStatus("Levante a mão para iniciar");
						}

						if (status == State.WAIT_FOR_START && yRight < 500) { // Executa
							status = State.WAIT_FOR_COMMAND;
							exibirStatus("Aguardando movimento");
							isPrimeiro = true;
						} else {
							double speedRight = (xRight - xRightOld) / 0.1;
							double speedLeft = (xLeft - xLeftOld) / 0.1;
							xRightOld = xRight;
							xLeftOld = xLeft;
							if (isPrimeiro) {
								isPrimeiro = false;
								escreveD = true;
								escreveE = true;
							} else {
								
								if (speedRight > speedLimit ) {
									mudarStatus(State.MOVING_RIGHT);
									if(escreveD){
										out = serialPort.getOutputStream();
										out.write(1);
										escreveD = false;
										escreveE = true;
									}
								} else if (speedLeft < -speedLimit) {
									mudarStatus(State.MOVING_LEFT);
									if(escreveE){
										out = serialPort.getOutputStream();
										out.write(0);
										escreveD = true;
										escreveE = false;
									}
								} else {
									mudarStatus(State.WAIT_FOR_COMMAND);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			commPort.close();
		}
	}

	private void mudarStatus(State s) {
		if(s == status) {
			return;
		}
		
		try {
			switch (s) {
			case WAIT_FOR_COMMAND:
				status = State.WAIT_FOR_COMMAND;
				break;
			case MOVING_RIGHT:
				exibirStatus("Direita");
				robot.keyPress(movDireita);
				status = State.MOVING_RIGHT;
				break;
			case MOVING_LEFT:
				exibirStatus("Esquerda");
				robot.keyPress(movEsquerda);
				status = State.MOVING_LEFT;
				break;
			case OUT_OF_RANGE:
				exibirStatus("Ninguém no alcance");
				status = State.OUT_OF_RANGE;
				break;
			case WAIT_FOR_START:
				exibirStatus("Levante a mão para iniciar");
				status = State.WAIT_FOR_START;
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void exibirStatus(String msg) {
		exibirStatus(msg, false);
	}

	protected static void exibirErro(String msg) {
	    JOptionPane.showMessageDialog(null, msg);
	    System.exit(0);
	}

	private void exibirStatus(String msg, boolean adapt) {
		mop.setText("Speed Limit: " + speedLimit
				+ "\nRight key code: " + rightKeyCodeStr
				+ "\nLeft key code: " + leftKeyCodeStr
				+ "\nCurrent status: " + msg, adapt);
	}

	public static void main(String args[]) {
		try{
			new KinectArduino();
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
