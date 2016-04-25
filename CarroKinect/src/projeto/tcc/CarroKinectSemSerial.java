package projeto.tcc;


import java.io.IOException;

import ch.aplu.jaw.NativeHandler;
import ch.aplu.kinect.Kinect;
import ch.aplu.kinect.Point3D;
import ch.aplu.kinect.SkeletonJoint;
import ch.aplu.util.ModelessOptionPane;

public class CarroKinectSemSerial 
{
	private String dllPath = Kinect.is64bit() ? "KinectHandler64" : "KinectHandler";
	private boolean flgEnviarDados = true;
	boolean maosLevantadas = false;
	private Estado estadoFT = null;
	private Estado estadoED = null;
	private Point3D leftHand;
	private Point3D rightHand;
	private ModelessOptionPane mop = new ModelessOptionPane(150,485, "Inicializando...");

	public CarroKinectSemSerial()
	{
		int articulacoes = 0;
		Point3D[] joints = new Point3D[20]; 
		mop.setTitle("CarroArduino");

		Kinect kinect = new Kinect(dllPath, "CarroKinect", 0, 0, 640, 480, NativeHandler.WS_DEFAULT);
		if (!kinect.isInitialized())
		{
			exibirStatusMop("Kinect não encontrado");
			kinect.setVisible(false);
		}

		kinect.setWindowScaleFactor(4);


		for (int i = 0; i < 20; i++){
			joints[i] = new Point3D();
		}
		exibirStatusMop("Aguardando um usuário válido...");
		kinect.getJoints(joints, 0);  

		while (true){
			   articulacoes = kinect.getJoints(joints, 20);

			   if (articulacoes == -1) {
			       exibirStatusMop("Aguardando um usuário válido...");
			       estadoFT = Estado.PARA;
			       estadoED = Estado.PARA;
			       continue;
			   }

			   leftHand = joints[SkeletonJoint.HAND_LEFT.ordinal()];
			   rightHand = joints[SkeletonJoint.HAND_RIGHT.ordinal()];
			   System.out.println(leftHand);
			   System.out.println(rightHand);
			   System.out.println(maosLevantadas);
			   if(leftHand.y<=300 && rightHand.y <=300 ){
			       if (!maosLevantadas) {
			               maosLevantadas = true;
			               enviarDadosValidos(flgEnviarDados);
			       }
			   }else{
			       maosLevantadas = false;
			       executaLeituraCorporal(rightHand, leftHand);
			       exibirStatusMop("Levante os dois braços para executar");
			   }
			}
	}



	private void executaLeituraCorporal(Point3D rightHand , Point3D leftHand) {
		System.out.println(flgEnviarDados);
		if(!flgEnviarDados){
			if (rightHand.y < 400){
				estadoFT = Estado.MOVE_TRAS;
			}else if(rightHand.y >1000){
				estadoFT = Estado.MOVE_FRENTE;
			}else if(rightHand.y <1000 && rightHand.y >400){
				estadoFT = Estado.PARA;
			}
			if (leftHand.x < 700){
				estadoED = Estado.VIRA_ESQUERDA;
			}else if(leftHand.x >1000){
				estadoED = Estado.VIRA_DIREITA;
			}else if(leftHand.x <1000 && leftHand.x >850){
				estadoED = Estado.PARA;
			}

		}

	}

	private boolean enviarDadosValidos(boolean flg){
		return flgEnviarDados = !flg;
	}

	private void exibirStatusMop(String msg){
		System.out.println(msg);
		if(!flgEnviarDados&&leftHand!=null && rightHand!=null && estadoED!=null && estadoFT!=null){
			mop.setText("\nEsquerda: "+leftHand + "\nDireita:" + rightHand + "\nEstado F_T:" + estadoFT.getDescricao()+
					"\nEstado D_E:" + estadoED.getDescricao() , true);
		}else{
			mop.setText(msg);
		}
	}
	
	private void executarComandosFT()
	{
		switch (estadoFT){
		case MOVE_FRENTE:
			//        envia arduino frente
			break;
		case MOVE_TRAS:
			//        envia arduino tras
			break;
		case PARA:
			//        envia arduino parar
			break;
		}
	}

	private void executarComandosED()
	{
		switch (estadoED){
		case VIRA_ESQUERDA:
			//        envia arduino esquerda
			break;
		case VIRA_DIREITA:
			//        envia arduino direita
			break;
		case PARA:
			//        envia arduino parar
			break;
		}
	}


	public static void main(String args[])
	{
		new CarroKinectSemSerial();
	}
}