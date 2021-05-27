# abort at error
set -e

# params from java
CONDA_INSTALL_DIR="$1"
ENV_NAME="$2"

# activate "base" conda
echo "activating conda installation in $CONDA_INSTALL_DIR"
source "$CONDA_INSTALL_DIR/etc/profile.d/conda.sh"
conda activate
sudo apt-get install libicu-dev

# activate env
echo "activating conda env in $ENV_NAME"
#conda create -n "$ENV_NAME"
conda activate "$ENV_NAME"
sudo apt-get install libicu-dev
echo "start python"
#python

# nltk
#python -m nltk.downloader punkt
#conda install numpy
#python install "$ENV_NAME" numpy
#conda install -n "$ENV_NAME" numpy
#python -m install numpy
#pip -m  install numpy
# install models
#echo "installing textblob/nltk corpora..."
#python -m textblob.download_corpora

#hello