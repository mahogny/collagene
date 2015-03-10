# MadGene

## Description

This is a simple piece of software for cloning, written in Java+QT

## Features

* Portable
   * Linux
   * Mac
   * Windows
* Restriction enzyme database
   * Temperatures and buffer information
   * Double-digests, suggestions for best common buffer
   * Simulated digests, with gel view and a range of ladders
* Linear and circular view
   * With smooth rotation and zooming
   * Live translation to protein
* Sequence property calculation
   * Melting temperature calculation, according to Santa lucia 98
   * GC content
* Primers and oligos
   * Annotation on sequence
   * Calculation of products
   * Simulated annealing of oligos
* Features
   * In different directions and colors
   * Automatic finding of ORFs
   
   TODO: automatic fitting of features
   
   
* Data import/export
   * Import from addgene
   * Import/export to genbank, including ApE variant      TODO: other software extensions
   * Import/export to fasta
   * Native XML format with extended metadata

* Cloning methods
   * TODO pcr/restriction
   * TODO gibson
   * TODO topo
   * TODO TA
   * TODO gateway

* General
   * Search for sequence
   * Turn plasmid around
   * Of course, handles restriction sites and features overlapping 0-position on circular plasmids
   * Copy out sequence in various directions (reverse, complement)
   * Skyline rendering of sequence
   * Align sequences
   
* TODO: reset 0-position of plasmid
* TODO: PDF export   


## About the sequence indices and ranges

Sequence ranges inside the code are 0-based and of the form [a,b). In other words, [0,1) selects the first base in a sequence and is of length 1. This choice is for practicality. It's simply faster and much less error prone in any modern programming language which also
have arrays starting with position 0.

The user-interface can view these indices in the form of 1-index [a,b] depending on user-preference (default is to use the internal format). This is to make it easy to compare positions within the software to those displayed by other programs. 


## License

Copyright (c) 2015, Johan Henriksson, EMBL-EBI and Karolinska Institutet
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
