/*******************************************************************************
 * Copyright 2012, Technical University of Crete
 * Autonomous Agents, winter semester 2011-12
 * Semester Assignment
 * 
 * @author Methenitis Giorgos
 * @author Mpountouris Konstantinos
 * @author Papadimitriou Maouro Vassilis
 * @author Skipetaris Dimosthenis 
 *******************************************************************************/

package agent;
import communication.HearMessage;
import communication.SendMessage;
import localization.BallPosition;
import motions.CurrentMotion;
import motions.MotionController;
import motions.MotionTrigger;
import behavior.Think;
import action.SeekBall;
import connection.Connection;
import connection.MessageController;
import connection.ServerCyrcles;
import worldState.GameState;
import java.lang.String;

import perceptor.isFallen;


public class Agent {


	public static int num=0;
	@SuppressWarnings("unused")
	private static CurrentMotion mt;
	@SuppressWarnings("unused")
	private static float max;

	public static String Teamname="";
	public static void main(String[] args) {


		Check Ch=new Check();
		MessageController Gp = new MessageController();
		SeekBall Sb = new SeekBall();
		SendMessage sm = new SendMessage();
		MotionController dnc=new MotionController();
		Think think=new Think();
		isFallen iF=new isFallen();
		//connection config
		//String host = args[0];
		String host = "127.0.0.1";
		//int port = Integer.parseInt(args[1]);
		int port=3100;
		//initializes the connection
		Connection con = new Connection(host,port);
		boolean isConnected = false;
		//establish the connection between agent and server
		isConnected = con.establishConnection();
		//Creation of Nao robot
		if(isConnected==true){
			InitAgent.CreateAgent(con);
		}
		//server cyrcles
		int i=0;
		max = 0;
		//player number
		//num=7;
		num=5;
		Teamname="tuc";
		// team name
		//String Teamname=args[2];
		//player position
		Ch.Number(num);


		while(con.isConnected()){


			i++;
			//update perceptors
			Gp.GetPerceptors(con);
			//update ball position
			BallPosition.WhereIsTheBall();
			//update server cyrcles
			ServerCyrcles.setCyrclesNow(i);
			//init Agent
			InitAgent.Init(Teamname, num, con);
			//think

			if(!GameState.getGameState().equalsIgnoreCase("BeforeKickOff") && InitAgent.isPlayerInited()==true){	
				think.Role(num);
				sm.Say("distance", con);
				HearMessage.MessageDecoder();
			}
			//check if i am down
			iF.Check();
			//get the head movement
			String headAct=Sb.MoveHead();
			//get the agent action
			String AgentAct= dnc.MotionFactory(MotionTrigger.getMotion());
			//create the hole agents actions
			String Act=headAct+AgentAct;
			//Act
			con.sendMessage(Act);




		}

	}
}
