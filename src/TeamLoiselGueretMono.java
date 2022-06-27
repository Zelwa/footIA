import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.abstractrobot.*;
import TeamLoiselGueretPackage.TeamMath;
import	java.util.Enumeration;


/**
 * Class TeamLoiselGueretMono
 * notre équipe se base sur un seul joueur
 * le joueur numéro 0
 *
 * il utilise les principes de positionnement derrière ou entre
 * une cible et une destination
 *
 * @author 21711075 LOISEL Lucas, 21700094 GUERET Morgan
 * @version $1.0$
 */
public class TeamLoiselGueretMono extends ControlSystemSS
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
	private Vec2 opponents[];

	/**
	 * Vecteur en direction du joueur actuel à partir du centre
	 */
	private Vec2 currentRobot;

	/**
	 * Vecteur en direction de la balle par rapport au joueur actuel
	 */
	private Vec2 ball;

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
		opponents = getOpponents();
		currentRobot = abstract_robot.getPosition(curr_time);
		ball = getBall();
		speed = 1.0;
	}


	/**
	 * Configure the Avoid control system.  This method is
	 * called once at initialization time.  You can use it
	 * to do whatever you like.
	 */
	@Override
	public void Configure() {}


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
	 * Retourne la liste des vecteurs des joueurs alliés au temps actuel
	 * @return la liste des vecteurs
	 */
	private Vec2[] getOpponents()
	{
		return abstract_robot.getOpponents(curr_time);
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
		Vec2 newTarget = TeamMath.toward(otherPlayer, target);
		Vec2 newDestination= TeamMath.toward(otherPlayer, destination);
		Vec2 vector = TeamMath.toward(newTarget, newDestination);
		Vec2 vector2 = new Vec2(newDestination);

		double angle = DSIZE / vector.r;
		double bottomAngle = TeamMath.correctAngleRange(vector.t + angle / 2);
		double topAngle = TeamMath.correctAngleRange(vector.t - angle / 2);

		double angle2 = DSIZE / vector2.r;
		double bottomAngle2 = TeamMath.correctAngleRange(vector2.t + angle2 / 2);
		double topAngle2 = TeamMath.correctAngleRange(vector2.t - angle2 / 2);

		if (ourside == -1)
			return TeamMath.isBetweenAngle(bottomAngle2, topAngle2
					, bottomAngle, topAngle);
		else
			return TeamMath.isBetweenAngle(topAngle2, bottomAngle2
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
		Vec2 towardVect = TeamMath.toward(destination, target);

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
		Vec2 towardVect = TeamMath.toward(destination, target);

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
	 * Retourne le vecteur représentant l'action que le joueur unique doit effectuer
	 * @return un vecteur
	 */
	private Vec2 monoIsBetter()
	{
		Vec2 result = new Vec2(ourgoal);
		abstract_robot.setDisplayString("to ourgoal");

		if (ball.r < 0.6 && ourgoal.r < 0.6)
		{
			result = betweenBallFromOurgoal();
			abstract_robot.setDisplayString("between ball");
		}
		else
		{
			result = behindBallFromTheirgoal();
			abstract_robot.setDisplayString("behind ball");
			if (!isBetweenTargetTeam(ball, theirgoal, opponents, BALL_RADIUS, GOAL_SIZE))
			{
				kick();
				abstract_robot.setDisplayString("kick");
			}
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
				result = monoIsBetter();
				break;
			default:
				result = currentRobot;
				break;
		}

		abstract_robot.setSteerHeading(curr_time, result.t);
		abstract_robot.setSpeed(curr_time, speed);

		return(CSSTAT_OK);
		}
	}
