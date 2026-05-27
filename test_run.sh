#!/bin/bash
echo "Test Wa-Tor: 1500x1500, 500 chronons"

for p in 1 2 4 8 12 16 20 24 28 32
do
    echo "-----------------------------------"
    echo "$p threads, test 1:"
    java Main $p 1500 1500 500    

    echo "$p threads, test 2:"
    java Main $p 1500 1500 500  
  
    echo "$p threads, test 3:"
    java Main $p 1500 1500 500
done

echo "\n-------------------------------------------------\n"
echo "Test Wa-Tor: 2000x2000, 500 chronons"

for p in 1 2 4 8 12 16 20 24 28 32
do
    echo "-----------------------------------"
    echo "$p threads, test 1:"
    java Main $p 2000 2000 500   

    echo "$p threads, test 2:"
    java Main $p 2000 2000 500
  
    echo "$p threads, test 3:"
    java Main $p 2000 2000 500
done

echo "\n-------------------------------------------------\n"
echo "Test Wa-Tor: 1500x1500, 1000 chronons"

for p in 1 2 4 8 12 16 20 24 28 32
do
    echo "-----------------------------------"
    echo "$p threads, test 1:"
    java Main $p 1500 1500 1000

    echo "$p threads, test 2:"
    java Main $p 1500 1500 1000 
  
    echo "$p threads, test 3:"
    java Main $p 1500 1500 1000
done

echo "\n-------------------------------------------------\n"
echo "Test Wa-Tor: 2000x2000, 1000 chronons"

for p in 1 2 4 8 12 16 20 24 28 32
do
    echo "-----------------------------------"
    echo "$p threads, test 1:"
    java Main $p 2000 2000 1000

    echo "$p threads, test 2:"
    java Main $p 2000 2000 1000
  
    echo "$p threads, test 3:"
    java Main $p 2000 2000 1000
done