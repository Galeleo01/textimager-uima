# abort at error
set -e

# params from java
CONDA_INSTALL_DIR="$1"
ENV_NAME="$2"

# activate "base" conda
echo "activating conda installation in $CONDA_INSTALL_DIR"
source "$CONDA_INSTALL_DIR/etc/profile.d/conda.sh"
conda activate
#polyglot download embeddings2.de transliteration2.ar
#polyglot download transliteration2.ar
#polyglot download pos2.de
#polyglot download ner2.de

# activate env
echo "activating conda env in $ENV_NAME"
#conda create -n "$ENV_NAME"
conda activate "$ENV_NAME"
#pip install numpy
#pip install polyglot
echo "install pyicu"
#pip install pyicu
echo "install pycld2"
#pip install pycld2
echo "install morfessor"
#pip install morfessor
echo "polyglot download emb"
polyglot download embeddings2.de
polyglot download transliteration2.ar
#JAVA_HOME=$6 pip install jep --no-cache-dir --force-reinstall
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