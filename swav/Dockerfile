FROM nvidia/cuda:10.1-cudnn7-devel-ubuntu18.04 AS BUILD

LABEL maintainer="alexander.komisov@exponenta.ru"
ARG PYTHON_VERSION=3.7
ARG CONDA_PYTHON_VERSION=3
ARG CONDA_DIR=/opt/conda
RUN apt-get update && \
    apt-get install -y --no-install-recommends git wget unzip bzip2 build-essential ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
Install miniconda
ENV PATH $CONDA_DIR/bin:$PATH
RUN wget --quiet https://repo.continuum.io/miniconda/Miniconda$CONDA_PYTHON_VERSION-latest-Linux-x86_64.sh -O /tmp/miniconda.sh && \
    echo 'export PATH=$CONDA_DIR/bin:$PATH' > /etc/profile.d/conda.sh && \
    /bin/bash /tmp/miniconda.sh -b -p $CONDA_DIR && \
    rm -rf /tmp/* && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN conda install -y python=$PYTHON_VERSION && \
    conda install -y pytorch torchvision cudatoolkit=10.1 -c pytorch && \
    conda install -y h5py scikit-learn matplotlib seaborn \
    pandas mkl-service cython && \
    conda clean -tipsy
#apex 
WORKDIR /tmp/
RUN git clone https://github.com/NVIDIA/apex.git && \
    cd apex && pip install -v --no-cache-dir --global-option="--cpp_ext" --global-option="--cuda_ext" .

RUN  pip install --upgrade pip && \
    pip install pillow-simd && \
    rm -rf ~/.cache/pip

# Runtime image
FROM nvidia/cuda:10.1-base

ARG CONDA_DIR=/opt/conda
ARG USERNAME=Ikariel
ARG USERID=1000

# Instal basic utilities
RUN apt-get update && \
    apt-get install -y --no-install-recommends git wget unzip bzip2 sudo nano p7zip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV PATH $CONDA_DIR/bin:$PATH
ENV CUDA_HOME=/usr/local/cuda
ENV CUDA_ROOT=$CUDA_HOME
ENV PATH=$PATH:$CUDA_ROOT/bin:$HOME/bin
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CUDA_ROOT/lib64

RUN mkdir -p /usr/src/swav/

# Create the user
RUN useradd --create-home -s /bin/bash --no-user-group -u $USERID $USERNAME && \
    chown $USERNAME $CONDA_DIR -R && \
    adduser $USERNAME sudo && \
    echo "$USERNAME ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers
USER $USERNAME
WORKDIR /home/$USERNAME

COPY --chown=1000 --from=build /opt/conda/. $CONDA_DIR
COPY . /usr/src/swav/
WORKDIR /usr/src/swav/swav-master
CMD python -m torch.distributed.launch --nproc_per_node=8 main_swav.py \
--data_path /use/src/swav/train \
--epochs 400 \
--base_lr 0.6 \
--final_lr 0.0006 \
--warmup_epochs 0 \
--batch_size 32 \
--size_crops 224 96 \
--nmb_crops 2 6 \
--min_scale_crops 0.14 0.05 \
--max_scale_crops 1. 0.14 \
--use_fp16 true \
--freeze_prototypes_niters 5005 \
--queue_length 3840 \
--epoch_queue_starts 15
