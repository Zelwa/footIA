GUERET Morgan  
LOISEL Lucas  

***

# DM Intelligence artificielle distribuée


Bienvenue sur notre readme.  

Il est disponible pour la compréhension et l'utilisation de nos fichiers pour faire jouer notre équipe   
principale *TeamLoiselGueret* ainsi que l'équipe monojoueur *TeamLoiselGueretMono*.  

## Organisation :

Le répertoire contient:
- *src/* : les fichiers de notre équipe ( *TeamLoiselGueret.java*, *TeamLoiselGueretMono.java*, *TeamLoiselGueretPackage/*  
- *dsc/* : les fichiers *robocup.dsc* pour tester les différents positionnement)  
- *backup/* : les fichiers de notre équipe mais sans l'utilisation du paquetage   
ainsi que des fichiers *.dsc*   
si jamais il y a un problème lors de l'utilisation du paquetage  
- *compile/* : un fichier *.bat* et un fichier *.sh* pour compiler à utiliser dans *TeamBots/*
  
  
**Important!** Le paquetage *TeamLoiselGueretPackage* est à mettre dans *TeamBots/src/*  
 Il contient les fonctions jugées mathématiques et indépendantes de la classe *ControlSystemSS*


## Lancement :

Pour utiliser notre équipe il faut au préalable placer correctement le paquetage  
Placer nos équipes disponibles dans *src/* dans votre répertoire *TeamBots/Domains/SoccerBots/teams/*  
Ne pas oublier de compiler le paquetage et les équipes  
Ensuite il suffit d'appeler nos équipes dans un fichier *.dsc*  
  
Si il y a un problème avec le paquetage, ne pas hésiter d'utiliser directement les équipes disponibles dans *backup/*