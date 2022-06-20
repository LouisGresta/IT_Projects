CC=gcc
CFLAGS=-std=c99 -Wextra -Wall -Werror -pedantic
EXEC=test
SRC=$(wildcard *.c)
OBJ= $(SRC:.c=.o)

all: $(EXEC)


%.o: %.c 
	$(CC) -o $@ -c $< $(CFLAGS)

$(EXEC): $(OBJ)
	$(CC) -o $@ $^ 

clean:
	rm -f *.o core

ZIP_FMT=$(shell date "+$(shell id -un)_%d-%m-%y_%Hh%Mm%Ss")

zip:
	zip -r $(DELIVER_FMT).zip .
