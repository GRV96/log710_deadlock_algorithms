# Algorithmes de LOG710 contre les interblocages
Ce dépôt contient des algorithmes enseignés dans le cours
[LOG710 *Principes des systèmes d’exploitation et programmation système*](https://www.etsmtl.ca/etudes/cours/LOG710)
à l’École de technologie supérieure (ÉTS), à Montréal. Leur objectif est de
détecter les interblocages (*deadlocks*) des processus exécutés par un système
d’exploitation et de les prévenir.

La présente implémentation de ces algorithmes n’est qu’une **simulation**:
aucun système d’exploitation ne peut l’utiliser pour contrôler les
interblocages. Elle ne sert qu’à observer les étapes et le résultat des
algorithmes.

Trois classes de ce dépôt, *DeadlockDetector*, *RequestEvaluator* et
*SafeSequenceMaker*, contiennent une méthode *main*. Elles serviront à générer
des exécutables .jar à appeler en ligne de commande avec le chemin d’un fichier
de données comme paramètre. L’extension de ce fichier doit être «.txt». Ces
exécutables créent dans leur dossier un fichier de résultats dont l’extension
est aussi «.txt».

La première ligne du fichier de données indique le nombre de processus
considérés (n); la deuxième indique le nombre de types de ressource (m)
auxquels ils ont accès. Le fichier de données et le fichier de résultats
contiennent des matrices de n rangées par m colonnes associant des données à
un processus (premier indice, celui des rangées) et à un type de ressource
(deuxième indice, celui des colonnes). Ils contiennent aussi des matrices
rangées dont la signification est énoncée plus loin. Les nombres sur une même
ligne d’une matrice sont séparés par des espaces. Il est primordial que n et m
soient cohérents avec les dimensions des matrices. Leurs indices commencent à 0.

Le dossier [cours_8_log710](/cours_8_log710) contient une explication des
interblocages, l'énoncé des algorithmes de ce dépôt et des exercices.

## Détecter les interblocages
Créez un exécutable .jar à partir de la classe *DeadlockDetector* et nommez-le
deadlock_detector.jar. Appelez-le en ligne de commande avec le chemin d’un
fichier de données comme paramètre. Par exemple, sur Windows, on peut appeler
deadlock_detector.jar avec les fichiers de données fournis dans ce dépôt comme
suit.

```
java -jar .\deadlock_detector.jar .\deadlock_detection1.txt
java -jar .\deadlock_detector.jar .\deadlock_detection2.txt
java -jar .\deadlock_detector.jar .\deadlock_detection3.txt
```

Le fichier de données définit les matrices *Resources*, *Allocation* et
*Request*. La matrice rangée *Resources*, de longueur m, indique le nombre de
ressources de chaque type impliquées dans la simulation. La matrice
*Allocation* indique le nombre de ressources de chaque type initialement
allouées à chaque processus. La matrice *Request* indique le nombre de
ressources que chaque processus demande au système. Le fichier de données
fourni à deadlock_detector.jar doit être conforme au format suivant, où «xi,j»
représente un entier naturel aux coordonnées (i, j) d’une matrice.

```
Processes: n
Resource types: m

Resources
x0,0 x0,1 x0,2 … x0,m

Allocation
x0,0 x0,1 x0,2 … x0,m
x1,0 x1,1 x1,2 … x1,m
x2,0 x2,1 x2,2 … x2,m
…
xn,0 xn,1 xn,2 … xn,m

Request
x0,0 x0,1 x0,2 … x0,m
x1,0 x1,1 x1,2 … x1,m
x2,0 x2,1 x2,2 … x2,m
…
xn,0 xn,1 xn,2 … xn,m
```

Dans le fichier de résultats, la matrice rangée *Available* indique le nombre
de ressources disponibles de chaque type. Chacune de ses cellules (0, j)
contient la différence entre la cellule (0, j) de *Resources* et la somme de
la colonne j d’*Allocation*. *End* est un tableau de n booléens indiquant si
un processus a été effectué (vrai) ou non (faux). Initialement, tous ses
éléments sont faux. La matrice rangée *Work*, de longueur m, est une donnée
servant à vérifier une condition dans l’algorithme de détection d’interblocage.
*Available* et *Work* contiennent seulement des entiers naturels.

L'algorithme de détection des interblocages est présenté aux pages 24 et 25 de
[cours_8_log710/LOG710_Hiver2020-Deadlocks.pdf](/cours_8_log710/LOG710_Hiver2020-Deadlocks.pdf).

## Prévenir les interblocages
Les classes *RequestEvaluator* et *SafeSequenceMaker* ont recours à
l’algorithme du banquier pour déterminer si l’allocation des ressources
demandées par un processus mettrait le système dans un état non sûr. L’état du
système est sûr si et seulement s’il est capable d’éviter les interblocages;
si son état est non sûr, les interblocages sont possibles, mais pas certains.
*RequestEvaluator* et *SafeSequenceMaker* ont besoin de fichiers de données
conformes au format suivant. Le fichier deadlock_prevention.txt est un exemple
d’entrée valide.

```
Processes: n
Resource types: m

Resources
x0,0 x0,1 x0,2 … x0,m

Allocation
x0,0 x0,1 x0,2 … x0,m
x1,0 x1,1 x1,2 … x1,m
x2,0 x2,1 x2,2 … x2,m
…
xn,0 xn,1 xn,2 … xn,m

Maximum
x0,0 x0,1 x0,2 … x0,m
x1,0 x1,1 x1,2 … x1,m
x2,0 x2,1 x2,2 … x2,m
…
xn,0 xn,1 xn,2 … xn,m
```

Ce format est identique à celui prescrit pour la détection d’interblocages
excepté qu’il exige une matrice *Maximum* plutôt que *Request*. *Maximum*
indique le nombre maximal de ressources de chaque type qu’un processus peut
demander au système d’exploitation.

Dans le fichier de résultats généré par *RequestEvaluator* et
*SafeSequenceMaker*, la matrice *Available* a la même signification que celle
calculée par *DeadlockDetector*. La matrice *Need* indique le nombre de
ressources supplémentaires dont un processus peut avoir besoin. La matrice
rangée *Work*, de longueur m, est une donnée servant à vérifier une condition
dans l’algorithme du banquier.

L'état sûr est défini aux pages 34 et 35 de
[cours_8_log710/LOG710_Hiver2020-Deadlocks.pdf](/cours_8_log710/LOG710_Hiver2020-Deadlocks.pdf).
Les pages 40 à 42 présentent l'algorithme du banquier et l'algorithme de
demande de ressources.

### Évaluer des requêtes
Créez un exécutable .jar à partir de la classe *RequestEvaluator* et nommez-le
request_evaluator.jar. Appelez-le en ligne de commande avec comme premier
paramètre le chemin d’un fichier de données et comme deuxième paramètre une
chaîne de caractères, dont la casse n’importera pas, exprimant une valeur
booléenne. Ce dernier paramètre détermine si le fichier de résultats contiendra
des données détaillées de l’algorithme du banquier. Pour la valeur *faux*, les
chaînes suivantes sont acceptées: «0», «f», «false», «n», «no»; pour la valeur
*vrai*, entrez une de ces chaînes: «1», «t», «true», «y», «yes». Sur Windows,
on peut exécuter request_evaluator.jar comme suit.

```
java -jar .\request_evaluator.jar .\deadlock_prevention.txt 0
java -jar .\request_evaluator.jar .\deadlock_prevention.txt y
```

En premier lieu, le programme indique dans la console si l’état initial du
système est sûr ou non. Puis, il demande à l’utilisateur d’entrer le numéro
d’un processus (de 0 à n-1, comme l’indice des rangées des matrices) et une
requête de ressources pour ce processus. L’entrée doit être constituée d’une
suite d’entiers naturels séparés par des espaces. Le premier nombre est le
numéro du processus; les suivants sont le nombre de ressources de chaque type
demandées. Si la requête n’engendre pas un état non sûr, le programme demande
à l’utilisateur s’il veut exécuter le processus. S’il le veut, les matrices
représentant l’état du système sont mises à jour en conséquence. Cette
séquence d’utilisation se répétera jusqu’à ce que l’utilisateur entre «q» à la
place d’une requête.

Dans le prochain exemple, l’utilisateur fait les requêtes suivantes:
1. Pour le processus 1
	* 1 ressource de type 0
	* 0 ressource de type 1
	* 2 ressources de type 2
2. Pour le processus 4
	* 3 ressources de type 0
	* 3 ressources de type 1
	* 0 ressource de type 2
3. Pour le processus 0
	* 0 ressource de type 0
	* 2 ressources de type 1
	* 0 ressource de type 2

```
The system's initial state is safe.

Process and request ("q" to quit): 1 1 0 2
Process 1 can be executed.
Do you want to execute it? n

Process and request ("q" to quit): 4 3 3 0
Executing process 4 would put the system in an unsafe state.

Process and request ("q" to quit): 0 0 2 0
Process 0 can be executed.
Do you want to execute it? n

Process and request ("q" to quit): q
```

Le fichier de résultats produit à la fin de l’exécution du programme contient
une copie du texte de la console et des données relatives à l’état du système
et à l’algorithme du banquier. Ces données sont plus détaillées si le deuxième
paramètre de la ligne de commande est vrai.

### Constituer une séquence sûre
Une séquence sûre est une séquence de processus qui peuvent être exécutés dans
l’ordre défini par la séquence sans interblocage. Un système d’exploitation
est dans un état sûr si et seulement si une séquence sûre existe.

Créez un exécutable .jar à partir de la classe *SafeSequenceMaker* et nommez-le
safe_sequence_maker.jar. Appelez-le en ligne de commande avec comme seul
paramètre le chemin d’un fichier de données comme dans l’exemple ci-dessous.

```
java -jar .\safe_sequence_maker.jar .\deadlock_prevention.txt
```

Ce programme exécute l’algorithme du banquier. Son fichier de résultats
contient, pour chaque itération, les données détaillées de l’algorithme,
telles que celles enregistrées par request_evaluator.jar si son deuxième
paramètre est vrai, et la séquence sûre déterminée par cette itération et les
précédentes. L’algorithme se termine après avoir constitué une séquence sûre
incluant tous les processus ou déterminé que c’est impossible. Dans ce dernier
cas, un message l’indique.
