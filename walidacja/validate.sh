mkdir -p unpacked/$1/validate
cp validate/Validate.java unpacked/$1/validate/
tar xzf packed/$1.tar.gz -C unpacked/$1/
cd unpacked/$1

if ! javac validate/Validate.java
then
    echo ERROR: compiling Validate
elif ! java validate/Validate
then
    echo ERROR: running Validate
fi
