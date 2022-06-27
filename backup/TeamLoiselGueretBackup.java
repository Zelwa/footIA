import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.abstractrobot.*;
import EDU.gatech.cc.is.communication.*;
import java.util.Enumeration;


/**
 * Class TeamLoiselGueretBackup
 * notre équipe se base sur le positionnement initial des joueurs
 * et l'attribution d'un numéro
 *
 * chaque joueur possède un rôle pré-définie
 *
 * nous retrouvons un gardien de but : le numéro 0
 * un défenseur reculé : le numéro 3
 * un défenseur avancé : le numéro 1
 * un attaquant principal : le numéro 2
 * un attaquant bloqueur : le numéro 4
 *
 * les joueurs utilisent les principes de positionnement derrière ou entre
 * une cible et une destination
 *
 * @author 21711075 LOISEL Lucas, 21700094 GUERET Morgan
 * @version $1.0$
 */
public class TeamLoiselGueretBackup extends ControlSystemSS
{

	/**
	 * Constantes
	 */

	/**
	 * Représente la taille de la balle
	 * disponible dans la page de présentation
	 */
 	private static final double BALL_RADIUS = 0.02;

	/**
	 * Représente la constante PI
	 */
	private static final double PI = Math.PI;

	/**
	 * Représente la taille d'un joueur
	 * disponible dans la page de présentation
	 */
	private final double ROBOT_RADIUS = abstract_robot.RADIUS;

	/**
	 * Représente la taille des buts
	 * disponible dans la page de présentation
	 */
	private static final double GOAL_SIZE = 0.5;

	/**
	 * Représente la taille maximale du terrain (la diagonale)
	 */
	private static final double DIAGONALE = 3.136;


	/**
	 * Variables
	 */

	/**
	 * Représente l'orientation de l'équipe du joueur actuel
	 * -1 pour ouest, 1 pour est
	 */
	private int ourside;

	/**
	 * Timestamp actuel
	 */
 	private long curr_time;

	/**
	 * Nouveau du joueur actuel dans l'équipe
	 */
	private int mynumber;

	/**
	 * Vecteur pointant vers le but du joueur actuel
	 */
	private Vec2 ourgoal;
	/**
	 * Vecteur pointant vers le but des adversaires
	 */
	private Vec2 theirgoal;

	/**
	 * Liste des vecteurs en direction des membres d'une équipe
	 */
	private Vec2 teammates[], opponents[];

	/**
	 * Vecteur en direction du joueur actuel à partir du centre
	 */
	private Vec2 currentRobot;

	/**
	 * Vecteur en direction de la balle par rapport au joueur actuel
	 */
	private Vec2 ball;

	/**
	 * Vecteur en direction de la balle par rapport au centre
	 */
	private Vec2 initialBall;

	/**
	 * Vecteur en direction du coéquipier numéro x au temps t - 1
	 */
	private Vec2 player0, player1, player2, player3, player4;

	/**
	 * La vitesse souhaitée pour l'action
	 */
	private double speed = 1.0;


	/**
	 * Initialisation des variables
	 */
  	private void init()
	{
		curr_time = abstract_robot.getTime();
		mynumber = abstract_robot.getPlayerNumber(curr_time);
		ourgoal = getOurGoal();
		theirgoal = getOpponentsGoal();
		ourside = ourgoal.x < 0 ? -1 : 1;
		teammates = getTeammates();
		opponents = getOpponents();
		currentRobot = abstract_robot.getPosition(curr_time);
		ball = getBall();
		speed = 1.0;
		initialBall = new Vec2(ball.x - currentRobot.x, ball.y - currentRobot.y);
		initMessages();
	}


	/**
	 * Initialisation avec les messages reçus
	 * les positions des alliées sont donc au temps t - 1
	 */
	private void initMessages()
	{
		Enumeration messagesin = abstract_robot.getReceiveChannel();
		while (messagesin.hasMoreElements())
		{
			PositionMessage recvd = (PositionMessage) messagesin.nextElement();
			switch (recvd.sender)
			{
				case 0:
					player0 = recvd.val;
					break;
				case 1:
					player1 = recvd.val;
					break;
				case 2:
					player2 = recvd.val;
					break;
				case 3:
					player3 = recvd.val;
					break;
				case 4:
					player4 = recvd.val;
					break;
			}
		}

		//envoie notre position actuelle au reste de l'équipe
		for (int i = 0; i < 5; i++)
		{
			sendPositionMessage(mynumber, i, currentRobot);
		}
	}


	/**
	 * Configure the Avoid control system.  This method is
	 * called once at initialization time.  You can use it
	 * to do whatever you like.
	 */
	@Override
	public void Configure()
	{
		//envoie notre position initiale au reste de l'équipe
		for (int i = 0; i < 5; i++)
		{
			sendPositionMessage(abstract_robot.getPlayerNumber(curr_time)
					, i, abstract_robot.getPosition(curr_time));
		}
	}


	/**
	 * Retourne le vecteur de la ball au temps actuel
	 * @return le vecteur de la balle
	 */
	private Vec2 getBall()
	{
		return abstract_robot.getBall(curr_time);
	}


	/**
	 * Retourne le vecteur de notre but au temps actuel
	 * @return le vecteur de notre but
	 */
	private Vec2 getOurGoal()
	{
		return abstract_robot.getOurGoal(curr_time);
	}


	/**
	 * Retourne le vecteur du but adverse au temps actuel
	 * @return le vecteur du but adverse
	 */
	private Vec2 getOpponentsGoal()
	{
		return abstract_robot.getOpponentsGoal(curr_time);
	}


	/**
	 * Retourne la liste des vecteurs des joueurs adverses au temps actuel
	 * @return la liste des vecteurs
	 */
	private Vec2[] getTeammates()
	{
		return abstract_robot.getTeammates(curr_time);
	}


	/**
	 * Retourne la liste des vecteurs des joueurs alliés au temps actuel
	 * @return la liste des vecteurs
	 */
	private Vec2[] getOpponents()
	{
		return abstract_robot.getOpponents(curr_time);
	}


	/**
	 * Envoyer un message string à un allié
	 * @param sender le numéro de l'envoyeur
	 * @param receiver le numéro du receveur
	 * @param val la string à envoyer
	 */
	private void sendStringMessage(int sender, int receiver, String val)
	{
		try
		{
			StringMessage sms = new StringMessage(val);
			sms.sender = sender;
			abstract_robot.unicast(receiver, sms);
		}
		catch(CommunicationException e){System.out.println("Send failed");}
	}


	/**
	 * Envoyer un message position à un allié
	 * @param sender le numéro de l'envoyeur
	 * @param receiver le numéro du receveur
	 * @param val le vecteur à envoyer
	 */
	private void sendPositionMessage(int sender, int receiver, Vec2 val)
	{
		try
		{
			PositionMessage sms = new PositionMessage(val);
			sms.sender = sender;
			abstract_robot.unicast(receiver, sms);
		}
		catch(CommunicationException e){System.out.println("Send failed");}
	}


	/**
	 * Envoyer un message register à un allié
	 * utile pour déterminer le type du message suivant
	 * @param sender le numéro de l'envoyeur
	 * @param receiver le numéro du receveur
	 * @param val l'entier à envoyer
	 */
	private void sendRegisterMessage(int sender, int receiver, int val)
	{
		try
		{
			RegisterMessage sms = new RegisterMessage(val);
			sms.sender = sender;
			abstract_robot.unicast(receiver, sms);
		}
		catch(CommunicationException e){System.out.println("Send failed");}
	}


	/**
	 * Retourne le vecteur du teammate le plus proche du joueur actuel
	 * @return le vecteur du teammate le plus proche
	 */
	private Vec2 getClosestTeammate()
	{
		return getClosestFromTeam(teammates);
	}


	/**
	 * Retourne le vecteur de l'adversaire le plus proche du joueur actuel
	 * @return le vecteur de l'adversaire le plus proche
	 */
	private Vec2 getClosestOpponent()
	{
		return getClosestFromTeam(opponents);
	}


	/**
	 * Retourne le joueur le plus proche du joueur actuel parmis une équipe
	 * @param players une équipe représentée par une liste de vecteur
	 * @return le vecteur du joueur le plus proche
	 */
	private Vec2 getClosestFromTeam(Vec2[] players)
	{
		Vec2 closestPlayer = new Vec2(99999,0);
		for (int i = 0; i < players.length; i++)
		{
			if (players[i].r < closestPlayer.r)
			{
				closestPlayer = players[i];
			}
		}
		return closestPlayer;
	}


	/**
	 * Retourne le joueur le plus proche d'une cible parmis une équipe
	 * @param target le vecteur de la cible
	 * @param players une équipe représentée par une liste de vecteur
	 * @param isMyTeam booléan pour savoir si l'équipe est celle du joueur actuel
	 * @return le vecteur du joueur le plus proche de la cible
	 */
	private Vec2 getClosestPlayerToTarget(Vec2 target, Vec2[] players, boolean isMyTeam)
	{
		if (isClosestToBallFromTeammates() && isMyTeam)
		{
			return currentRobot;
		}
		else
		{
			Vec2 closestPlayer = new Vec2(99999,0);
			closestPlayer.r = 99999;
			for (int i = 0; i < players.length; i++)
			{
				Vec2 copyPlayer = toward(target, players[i]);
				if (copyPlayer.r < closestPlayer.r)
				{
					closestPlayer = new Vec2(copyPlayer.x, copyPlayer.y);
					closestPlayer.setr(copyPlayer.r);
				}
			}
			return closestPlayer;
		}
	}


	/**
	 * Retourne le joueur le plus proche de la balle parmis les alliés
	 * @return le vecteur du joueur le plus proche de la balle
	 */
	private Vec2 getClosestTeammateToBall(){
		return getClosestPlayerToTarget(ball, teammates, true);
	}


	/**
	 * Retourne le joueur le plus proche de la balle parmis les adversaires
	 * @return le vecteur du joueur le plus proche de la balle
	 */
	private Vec2 getClosestOpponentToBall(){
		return getClosestPlayerToTarget(ball, opponents, false);
	}


	/**
	 Retourne le joueur le plus proche parmis une équipe
	 */
	private Vec2 getClosestTeammatesStriker(Vec2[] team)
	{
		Vec2 closestPlayer = new Vec2(99999,0);
		for (int i = 0; i < team.length; i++)
		{
			if (team[i].r < closestPlayer.r && team[i].x * (- ourside) > 0)
			{
				closestPlayer = team[i];
			}
		}
		return closestPlayer;
	}


	/**
	 * Vérifie si le joueur est le plus proche d'une cible par rapport à une équipe
	 * @param target le vecteur de la cible
	 * @param players une équipe représentée par une liste de vecteur
	 * @return le joueur est le plus proche de la cible
	 */
	private boolean isClosestTo(Vec2 target, Vec2[] players)
	{
		for (int i = 0; i < players.length; i++)
		{
			Vec2 copyPlayer = toward(target, players[i]);
			if (copyPlayer.r < target.r)
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Vérifie si le joueur est le plus de la balle par rapport à sa team
	 * @return le joueur est le plus proche de la balle
	 */
  	private boolean isClosestToBallFromTeammates()
	{
		return isClosestTo(ball, teammates);
	}


	/**
	 * Vérifie si le joueur est le plus de la balle par rapport à la team adverse
	 * @return le joueur est le plus proche de la balle
	 */
	private boolean isClosestToBallFromOpponents()
	{
		return isClosestTo(ball, opponents);
	}


	/**
	 * Vérifie si le joueur est le plus de la balle par rapport à sa team et à la team adverse
	 * @return le joueur est le plus proche de la balle
	 */
	private boolean isClosestToBall()
	{
		return isClosestToBallFromTeammates() && isClosestToBallFromOpponents();
	}


	/**
	 * Tirer si possible
	 */
	private void kick()
	{
		if (canKick()) //Eviter les bugs
		{
			abstract_robot.kick(curr_time);
		}
	}


	/**
	 * Vérifie si le joueur actuel peut tirer
	 * @return le joueur actuel peut tirer
	 */
	private boolean canKick()
	{
		return abstract_robot.canKick(curr_time);
	}


	/**
	 * Retourne un vecteur pointant du vecteur a vers le vecteur b
	 * @param a le vecteur source
	 * @param b le vecteur cible
	 * @return le vecteur de a vers b
	 */
	public static Vec2 toward(Vec2 a, Vec2 b)
	{
		Vec2 copy = new Vec2(b.x, b.y);
		copy.sub(a);
		return copy;
	}


	/**
	 * Conversion de degree à Radian
	 * @param angle l'angle à convertir
	 * @return l'angle converti
	 */
	public static double degreeToRadian(double angle)
	{
		return Math.sin(angle * Math.PI/180);
	}


	/**
	 * Etant donnée une valeur, un interval initial et un interval final
	 * retourne la valeur dans l'interval final
	 * @param value la valeur initial
	 * @param fmin la borne inférieure de l'interval final
	 * @param fmax la borne supérieure de l'interval final
	 * @param imin la borne inférieure de l'interval initial
	 * @param imax la borne supérieure de l'interval initial
	 * @return la valeur dans le nouvel interval
	 */
	public static double transformRange(double value, double fmin, double fmax
			, double imin, double imax)
	{
		double scale = (fmax - fmin) / (imax - imin);
		return (value - imin) * scale;
	}


	/**
	 * Retourne un angle exprimé entre 0 et 2*PI radian
	 * @param angle l'angle à corriger
	 * @return l'angle corrigé
	 */
	public static double correctAngleRange(double angle)
	{
		while (angle < 0) angle += 2 * PI;
		while (angle > 2 * PI) angle -= 2 * PI;
		return angle;
	}


	/**
	 * Vérifie qu'un angle soit compris dans un autre angle
	 * ex un angle de 70° peut-être compris dans un angle de 90°
	 * @param a angle bas par rapport à la cible et la destination
	 * @param b angle haut par rapport à la cible et la destination
	 * @param e angle bas par rapport au joueur et la destination
	 * @param f angle haut par rapport au joueur et la destination
	 * @return si e f sont compris entre a et b
	 */
	public static boolean isBetweenAngle(double a, double b, double e, double f)
	{
		return (a > e && b < f);
	}


	/**
	 * Vérifie que le joueur actuel soit derrière une cible par rapport à une destination
	 * @param target le vecteur de la cible
	 * @param destination le vecteur de la destination
	 * @param TSIZE la taille de la cible
	 * @param DSIZE la taille de la destination
	 * @return le joueur est derrière
	 */
	private boolean isBehind(Vec2 target, Vec2 destination, double TSIZE, double DSIZE)
	{
		Vec2 vector = toward(target, destination);
		Vec2 vector2 = new Vec2(destination);

		double angle = DSIZE / vector.r;
		double bottomAngle = correctAngleRange(vector.t + angle / 2);
		double topAngle = correctAngleRange(vector.t - angle / 2);

		double angle2 = DSIZE / vector2.r;
		double bottomAngle2 = correctAngleRange(vector2.t + angle2 / 2);
		double topAngle2 = correctAngleRange(vector2.t - angle2 / 2);

		if (ourside == -1)
			return isBetweenAngle(bottomAngle, topAngle
				  , bottomAngle2, topAngle2);
		else
			return isBetweenAngle(topAngle, bottomAngle
					, topAngle2, bottomAngle2);
	}


	/**
	 * Vérifie que le joueur actuel soit entre une cible et une destination
	 * @param target le vecteur de la cible
	 * @param destination le vecteur de la destination
	 * @param TSIZE la taille de la cible
	 * @param DSIZE la taille de la destination
	 * @return le joueur est entre
	 */
	private boolean isBetween(Vec2 target, Vec2 destination, double TSIZE, double DSIZE)
	{
		Vec2 vector = toward(target, destination);
		Vec2 vector2 = new Vec2(destination);

		double angle = DSIZE / vector.r;
		double bottomAngle = correctAngleRange(vector.t + angle / 2);
		double topAngle = correctAngleRange(vector.t - angle / 2);

		double angle2 = DSIZE / vector2.r;
		double bottomAngle2 = correctAngleRange(vector2.t + angle2 / 2);
		double topAngle2 = correctAngleRange(vector2.t - angle2 / 2);

		if (ourside == -1)
			return isBetweenAngle(bottomAngle2, topAngle2
					, bottomAngle, topAngle);
		else
			return isBetweenAngle(topAngle2, bottomAngle2
					, topAngle, bottomAngle);
	}


	/**
	 * Vérifie q'un joueur soit entre une cible et une destination
	 * @param target le vecteur de la cible
	 * @param destination le vecteur de la destination
	 * @param otherPlayer le vecteur du joueur
	 * @param TSIZE la taille de la cible
	 * @param DSIZE la taille de la destination
	 * @return le joueur est entre
	 */
	private boolean isBetweenOtherPlayer(Vec2 target, Vec2 destination, Vec2 otherPlayer, double TSIZE, double DSIZE)
	{
		Vec2 newTarget = toward(otherPlayer, target);
		Vec2 newDestination= toward(otherPlayer, destination);
		Vec2 vector = toward(newTarget, newDestination);
		Vec2 vector2 = new Vec2(newDestination);

		double angle = DSIZE / vector.r;
		double bottomAngle = correctAngleRange(vector.t + angle / 2);
		double topAngle = correctAngleRange(vector.t - angle / 2);

		double angle2 = DSIZE / vector2.r;
		double bottomAngle2 = correctAngleRange(vector2.t + angle2 / 2);
		double topAngle2 = correctAngleRange(vector2.t - angle2 / 2);

		if (ourside == -1)
			return isBetweenAngle(bottomAngle2, topAngle2
					, bottomAngle, topAngle);
		else
			return isBetweenAngle(topAngle2, bottomAngle2
					, topAngle, bottomAngle);
	}


	/**
	 * Vérifie si le joueur d'une équipe est entre une cible et une destination
	 * @param target le vecteur de la cible
	 * @param destination le vecteur de la destination
	 * @param players une équipe représentée par une liste de vecteur
	 * @param TSIZE la taille de la cible
	 * @param DSIZE la taille de la destination
	 * @return le joueur est entre
	 */
	private boolean isBetweenTargetTeam(Vec2 target, Vec2 destination, Vec2[] players, double TSIZE, double DSIZE)
	{
		for (int i = 0; i < players.length; i++)
		{
			if (isBetweenOtherPlayer(target, destination, players[i], TSIZE, DSIZE))
			{
				return true;
			}
		}
		return false;
	}



	/**
	 * Retourne un vecteur vers une position derrière la cible
	 * par rapport à la destination
	 * @param target le vecteur de la cible
	 * @param destination le vecteur de la destination
	 * @param TSIZE la taille de la cible
	 * @param DSIZE la taille de la destination
	 * @return le vecteur vers la position
	 */
	private Vec2 behindTarget(Vec2 target, Vec2 destination, double TSIZE, double DSIZE)
	{
		Vec2 result = new Vec2(target);
		Vec2 towardVect = toward(destination, target);

		towardVect.setr(towardVect.r + (ROBOT_RADIUS * 0.5 + TSIZE));
		towardVect.add(destination);

		result.sett(towardVect.t);

		return result;
	}


	/**
	 * Retourne un vecteur vers une position entre la cible et la destination
	 * @param target le vecteur de la cible
	 * @param destination le vecteur de la destination
	 * @param TSIZE la taille de la cible
	 * @param DSIZE la taille de la destination
	 * @return le vecteur vers la position
	 */
	private Vec2 betweenTarget(Vec2 target, Vec2 destination, double TSIZE, double DSIZE)
	{
		Vec2 result = new Vec2(target);
		Vec2 towardVect = toward(destination, target);

		towardVect.setr(towardVect.r - (ROBOT_RADIUS * 0.5 + TSIZE));
		towardVect.add(destination);

		result.sett(towardVect.t);

		return result;
	}


	/**
	 * Retourne un vecteur vers une position derrière la balle
	 * par rapport au but adverse
	 * @return le vecteur vers la position
	 */
	private Vec2 behindBallFromTheirgoal()
	{
		return behindTarget(ball, theirgoal, BALL_RADIUS, GOAL_SIZE);
	}


	/**
	 * Retourne un vecteur vers une position entre la balle
	 * et le but du joueur actuel
	 * @return le vecteur vers la position
	 */
	private Vec2 betweenBallFromOurgoal()
	{
		return betweenTarget(ball, ourgoal, BALL_RADIUS, GOAL_SIZE);
	}


	/**
	 * Retourne un vecteur vers une position derrière l'allié le plus proche
	 * par rapport à la destination
	 * @return le vecteur vers la position
	 */
	private Vec2 behindNearestTeammate()
	{
		return betweenTarget(getClosestTeammate(), ourgoal, ROBOT_RADIUS, GOAL_SIZE);
	}


	/**
	 * Vérifie si un joueur peut recevoir une passe
	 * (que aucun joueur adverse ne soit entre )
	 * @param player le vecteur d'un joueur
	 * @return peut recevoir une passe
	 */
	private boolean isPassable(Vec2 player)
	{
		return !isBetweenTargetTeam(ball, player, getOpponents(),BALL_RADIUS, ROBOT_RADIUS);
	}


	/**
	 * Vérifie si au moins un joueur peut recevoir une passe
	 * @param team la liste des vecteur des alliés
	 * @return au moins un joueur peut recevoir une passe
	 */
	private boolean playerInFront(Vec2[] team)
	{
		for (int i=0; i<team.length; i++)
		{
			if(!isPassable(team[i]) && toward(team[i], ourgoal).r < 0.2)
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Retourne le vecteur représentant l'action que le goal doit effectuer
	 * @return un vecteur
	 */
	private Vec2 goalIsBetter()
	{
		Vec2 result = new Vec2(ourgoal);
		abstract_robot.setDisplayString("to ourgoal");

		if (ball.r < 0.6 && ourgoal.r < 0.6)
		{
			result = betweenBallFromOurgoal();
			abstract_robot.setDisplayString("between ball");
			if (playerInFront(teammates))
			{
				kick();
				abstract_robot.setDisplayString("kick");
			}
		}

		return result;
	}


	/**
	 * Retourne le vecteur représentant l'action que le défenseur doit effectuer
	 * @return un vecteur
	 */
	private Vec2 defenderIsBetter()
	{
		Vec2 result;

		if (mynumber == 1)
		{
			if(toward(ball, ourgoal).r < 1.4)
			{
				result = betweenBallFromOurgoal();
				abstract_robot.setDisplayString("between ball");
				if (isClosestToBall())
				{
					if (!isBetweenTargetTeam(ball, theirgoal, opponents, BALL_RADIUS, GOAL_SIZE))
					{
						kick();
						abstract_robot.setDisplayString("kick");
					}
				}
			}
			else
			{
				result = betweenTarget(new Vec2 (- currentRobot.x, - currentRobot.y), ourgoal, BALL_RADIUS, GOAL_SIZE);
				abstract_robot.setDisplayString("to center");
				if (isBetween(theirgoal, ourgoal, GOAL_SIZE, GOAL_SIZE) && currentRobot.r < 0.12)
				{
					if (ourgoal.r < 1.3)
					{
						result.sett(ball.t);
						speed = 0;
						abstract_robot.setDisplayString("waiting center");
					}
					else
					{
						result = ourgoal;
						abstract_robot.setDisplayString("to ourgoal");
					}

				}
				if (isClosestToBall())
				{
					speed = 1;
					result = behindBallFromTheirgoal();
					abstract_robot.setDisplayString("behind ball");
					if (!isBetweenTargetTeam(ball, theirgoal, opponents, BALL_RADIUS, GOAL_SIZE))
					{
						kick();
						abstract_robot.setDisplayString("kick");
					}
				}
			}
		}
		else if (mynumber == 3)
		{
			if(isClosestToBall() || toward(ball, ourgoal).r < 1.4){
				result = behindBallFromTheirgoal();
				abstract_robot.setDisplayString("behind ball");
				if (!isBehind(ball, ourgoal, BALL_RADIUS, GOAL_SIZE)
						&& !isBetweenTargetTeam(ball, theirgoal, opponents, BALL_RADIUS, GOAL_SIZE))
				{
					kick();
					abstract_robot.setDisplayString("kick");
				}
			}
			else
			{
				result = ourgoal;
				abstract_robot.setDisplayString("to ourgoal");
				if (ourgoal.r < 0.6) {
					if (ourgoal.r > 0.3)
					{
						result.sett(ball.t);
						speed = 0;
						abstract_robot.setDisplayString("waiting center");
					}
					else
					{
						result = new Vec2(- currentRobot.x, - currentRobot.y);
						abstract_robot.setDisplayString("to center");
					}
				}
			}
		}
		else
			result = betweenBallFromOurgoal();

		Vec2 closestTeammate = getClosestTeammate();

		if (closestTeammate.r < 0.1)
		{
			Vec2 copyClosest = new Vec2(0, closestTeammate.y * -1);
			result = copyClosest;
		}

		return result;
	}


	/**
	 * Retourne le vecteur représentant l'action que l'attaquant doit effectuer
	 * @return un vecteur
	 */
	private Vec2 strikerIsBetter()
	{
		Vec2 result;

		if (mynumber == 2)
		{
			if(toward(ball,ourgoal).r > 1.2)
			{
				result = behindBallFromTheirgoal();
				abstract_robot.setDisplayString("behind ball");
				if (!isBetweenTargetTeam(ball, theirgoal, getOpponents(), BALL_RADIUS, GOAL_SIZE) && isClosestToBall()) {
					kick();
					abstract_robot.setDisplayString("kick");
				}
			}
			else
			{
				result = betweenTarget(new Vec2 (- currentRobot.x, - currentRobot.y), ourgoal, BALL_RADIUS, GOAL_SIZE);
				abstract_robot.setDisplayString("to center");
				if (isBetween(theirgoal, ourgoal, GOAL_SIZE, GOAL_SIZE) && currentRobot.r < 0.12)
				{
					if (ourgoal.r < 1.3)
					{
						result.sett(ball.t);
						speed = 0;
						abstract_robot.setDisplayString("waiting center");
					}
					else
					{
						result = ourgoal;
						abstract_robot.setDisplayString("to ourgoal");
					}

				}
			}
		}
		else if (mynumber == 4)
		{
			if (isClosestToBallFromTeammates())
			{
				result = behindBallFromTheirgoal();
				abstract_robot.setDisplayString("behind ball");
				if (!isBetweenTargetTeam(ball, theirgoal, getOpponents(), BALL_RADIUS, GOAL_SIZE)) {
					kick();
					abstract_robot.setDisplayString("kick");
				}
			}
			else if(getClosestTeammateToBall().r < 0.8 && toward(ball,ourgoal).r > 1.6
					&& theirgoal.r > 0.3 && ourgoal.r > 1.4
					&& (getClosestOpponent().x * ourside > currentRobot.x * ourside))
			{
				result = behindTarget(getClosestOpponent(), ourgoal, ROBOT_RADIUS, GOAL_SIZE);
				if(isBetween(getClosestOpponent(), theirgoal, ROBOT_RADIUS, GOAL_SIZE))
				{
					result=getClosestOpponent();
					abstract_robot.setDisplayString("to opponent");
				}
				speed = 1;
				abstract_robot.setDisplayString("follow opponent");
			}
			else
			{
				result = theirgoal;
				abstract_robot.setDisplayString("to theirgoal");
				if (theirgoal.r < 0.6) {
					if (theirgoal.r > 0.3)
					{
						result.sett(ball.t);
						speed = 0;
						abstract_robot.setDisplayString("waiting attack");
						if (isBehind(ball, player2, BALL_RADIUS, ROBOT_RADIUS))
						{
							speed = 1;
							abstract_robot.setDisplayString("between friend");
						}
					}
					else
					{
						result = new Vec2(- currentRobot.x, - currentRobot.y);
						abstract_robot.setDisplayString("to center");
					}
				}
			}
		}
		else
			result = behindBallFromTheirgoal();

		Vec2 closestTeammate = getClosestTeammate();

		if (closestTeammate.r < 0.1)
		{
			Vec2 copyClosest = new Vec2(closestTeammate.x * -1, closestTeammate.y * -1);
			result = copyClosest;
		}
		return result;
	}


	/**
	 * Appelé à chaque timestep pour déterminer l'action du joueur actuel
	 * @return un état
	 */
	@Override
	public int TakeStep()
	{
		init();
		Vec2 result;

		switch(mynumber) {
			case 0:
				result = goalIsBetter();
				break;
			case 1:
				result = defenderIsBetter();
				break;
			case 2:
				result = strikerIsBetter();
				break;
			case 3:
				result = defenderIsBetter();
				break;
			case 4:
				result = strikerIsBetter();
				break;
			default:
				result = ball;
				break;
		}

		//TeamClosest.faire();

		abstract_robot.setSteerHeading(curr_time, result.t);
		abstract_robot.setSpeed(curr_time, speed);

		return(CSSTAT_OK);
		}
	}
