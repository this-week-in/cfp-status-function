#!/usr/bin/env bash

mkdir -p $HOME/.pip/repository/
pip install --cache-dir $HOME/.pip/repository/ --user awscli==1.14.9
pip install --cache-dir $HOME/.pip/repository/ --user aws-sam-cli==0.5.0
export PATH=$PATH:$HOME/.local/bin

source ./deploy.sh