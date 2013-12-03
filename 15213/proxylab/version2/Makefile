CC = gcc
CFLAGS = -g -Wall
LDFLAGS = -lpthread

all: proxy

csapp.o: csapp.c csapp.h
	$(CC) $(CFLAGS) -c csapp.c

help.o: help.c help.h
	$(CC) $(CFLAGS) -c help.c

proxy.o: proxy.c csapp.h
	$(CC) $(CFLAGS) -c proxy.c

cache.o: cache.c cache.h

proxy: proxy.o csapp.o help.o cache.o

submit:
	(make clean; cd ..; tar cvf proxylab.tar proxylab-handout)

clean:
	rm -f *~ *.o proxy core

