CC=gcc
CFLAGS=-std=c99 -Wextra -Wall -Werror -pedantic

ifeq ($(DEBUG),yes)
	CFLAGS += -g
endif

EXEC=test
SRC=$(wildcard */*.c)
DOC=$(wildcard */*.h)
OBJ=$(SRC:.c=.o)

all: $(EXEC) clean

%.o: %.c 
	$(CC) -o $@ -c $< $(CFLAGS)

$(EXEC): $(OBJ) main.o
	$(CC) -o $@ $^ 

doc: $(DOC) main.c
	@doxygen documentation/ProgC

clean:
	rm -f */*.o core
	rm -f *.o core

mrproper: clean
	@rm -rf $(EXEC) documentation/html
