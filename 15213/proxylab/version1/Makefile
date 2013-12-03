CC = gcc
CFLAGS = -g -Wall
#CFLAGS = -g
LDFLAGS = -lpthread

all: proxy

csapp.o: csapp.c csapp.h
	$(CC) $(CFLAGS) -c csapp.c

proxy.o: proxy.c proxy.h cache.h csapp.h
	$(CC) $(CFLAGS) -c proxy.c

cache.o: cache.c cache.h csapp.h
	$(CC) $(CFLAGS) -c cache.c

proxy: proxy.o cache.o csapp.o
	$(CC) $(CFLAGS) -o proxy proxy.o cache.o csapp.o $(LDFLAGS)

submit:
	(make clean; cd ..; tar czvf proxylab.tar.gz proxylab-handout)

clean:
	rm -f *~ *.o proxy core

