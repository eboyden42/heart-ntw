# heart-ntw

## Overview
This project was created after finishing a project where I built a framework to create transformer neural networks.
The goal of this project was to use real world data and attempt to fit a model to the data. The project uses a basic
dataset for heart attack prediction that can be found on Kaggle. The dataset contains 13 biometric indicators, and
an output column denoting the occurrence of a heart attack. The goal of the network is to predict these heart attacks
based on the input data.

### Network Creation
To create a new network simply run the program, and enter a network name that doesn't already exist in the project directory.
If your name does not have a ".ntw" at the end, one will be appended. You can then choose to create a new network with
default settings, or specify the number of layers and nodes yourself.

### Training
Newly created networks, or existing ones if you choose, are trained on Kaggle heart attack training data for a default of 10000 epochs
(this can be changed however, view Extra Settings below), where one epoch is one pass through the training data. Generally
this will get you pretty good results, although as you try different sizes of networks you will find that some
networks perform much worse than others, and networks that are too large may cause problems in the engine (see Issues). It
should go without saying that the larger networks will take longer to train than the smaller ones, so be prepared to wait
if you want to train large networks.

### Analysing Trained Networks
After a new network is trained some basic information regarding the networks performance will appear. Note that with
small data sets like this one training results will be varied

### Loading Preexisting Networks
After creating a network it will be saved in the root directory as "network_name.ntw". You can reuse these networks
by entering their name into the prompt when the program is run (with or without the .ntw). You may then choose if you want
to train the network, or simply continue analysis. There are also a few pre-existing networks which I have had success
training in the past. You can find these in the root directory and you are free to analyse them or train them further
if you wish.

## Author
Eli Boyden, aqu4eb, eboyden42
Completed after following course "Create a Neural Network in Java" by John Purcell, added additional functionality afterwards
https://www.udemy.com/course/neural-network-java

## To Run

1) Clone the repository and navigate to src/main/java/research/HeartPredictionApp.java.
2) Run the app and follow directions in the terminal to either create a new network or use an existing one. New networks
   will automatically train and then generate images and predictions.

## Issues

If network sizes become too large then the Loss may appear as NaN. This is not an issue in most cases, however,
it is probably wise to end the program and try again with a smaller network. You could try adjusting the scale initial weights
to a smaller value (Extra Settings), but caution is advised.

## Extra Settings

Lines 45 to 50 in HeartPredictionApp.java contain variables that act as training settings, and you may change them at your discretion.
The two most useful of these will be number of epochs, and the learning rate variables.