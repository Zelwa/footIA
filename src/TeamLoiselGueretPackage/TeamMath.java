package TeamLoiselGueretPackage;

import EDU.gatech.cc.is.util.Vec2;

/**
 * Class TeamMath
 * cette classe regroupe les fonctions mathématiques nécessaires
 * au fonctionnement de l'équipe TeamLoiselGueret
 */
public class TeamMath
{
    /**
     * Représente la constante PI
     */
    private static final double PI = Math.PI;

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
}