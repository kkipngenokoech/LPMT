#!/usr/bin/bash

userFile="user.txt"
# Function to create a user
createUser() {
    local username=$1
    local role=$2
    local uid==$(uuidgen)

    # Check if user already exists
    if grep -qs "^$username$" "$userFile" ; then
        echo "User $username already exists in our database."
    else
        # Create user and set role
        echo "$uid:$username:$role" >> "$userFile"
        echo "UUID $uid generated"
    fi
}

# Function to create an initial admin
createAdminZero() {
    local adminZeroUsername="admin"
    local adminPassword="A1234"
    
    
    if grep -qs "^$adminZeroUsername" "$userFile"; then
        :
    else
        createUser "$adminZeroUsername" "$adminPassword" "admin"
    fi
}

adminFunctions(){
    echo "1. Create a new user"
    echo "2. Export user data"
    echo "3. Delete a user"
    echo "4. Manage user data"
    echo "5. Logout"
    read -p "Enter your choice: " choice

    case $choice in
        1)
            read -p "Enter username: " username
            read -sp "Enter password: " password
            echo
            read -p "Enter role: " role
            createUser "$username" "$password" "$role"
            adminFunctions
            ;;
        2)
            echo "Exporting user data..."
            adminFunctions
            ;;
        3)
            read -p "Enter username to delete: " username
            sed -i "/^$username/d" "$userFile"
            echo "User $username deleted."
            adminFunctions
            ;;
        4)
            cat "$userFile"
            adminFunctions
            ;;
        5)
            loginUser
            ;;
        *)
            echo "Invalid choice. Please try again."
            adminFunctions
            ;;
    esac
}

patientFunctions(){
    echo "1. View your profile"
    echo "2. Update your profile"
    echo "3. Compute life expectancy"
    echo "4. Logout"
    read -p "Enter your choice: " choice

    case $choice in
        1)
            echo "Viewing profile"
            patientFunctions
            ;;
        2)
            echo "Updating your profile"
            patientFunctions

            ;;
        3)
            echo "Time is catching up with you. Computing life expectancy..."
            patientFunctions

            ;;
        4)
            loginUser
            ;;
        *)
            echo "Invalid choice. Please try again."
            patientFunction
            ;;
    esac
}


loginUser() {
    local username=$1
    # local username=$1
    local password=$2
    # hashing the password

    local hashedPassword=$(echo "$password" | sha256sum)

    if grep -qE ":$username:$hashedPassword:" "$userFile"; then
        local userRecord=$(grep -E ":$username:$hashedPassword:" "$userFile")
        local role=$(echo "$userRecord" | cut -d':' -f4)
        local uuid=$(echo "$userRecord" | cut -d':' -f1)
        echo "$role:$uuid"
    else
        echo "$username:$hashedPassword"
        # echo "Invalid username or password. Please try again."
    fi
}

checkUUID(){
    local uid=$1
    # grep for uuid in the user.txt file
     if grep -qE "^$uid:" "$userFile"; then
        echo "UUID exists"
        return 0
    else
        :
    fi
}
completeRegistration(){
    uuid=$2
    username=$3
    password=$4
    firstName=$5
    lastName=$6
    email=$7
    dateofinfection=$8
    onMedication=$9
    starDateofMedication=${10}
    dob=${11}
    country=${12}
    role=${13}
    password=$(echo "$password" | sha256sum)
    if grep -q "^$uuid:" "$userFile"; then
        # UUID exists, delete the line
        sed -i "/^$uuid:/d" "$userFile"
    fi

    # Append the new data
    echo "$uuid:$username:$password:$role:$firstName:$lastName:$email:$dateofinfection:$onMedication:$starDateofMedication:$dob:$country" >> "$userFile"
    echo "User $username with $uuid updated."
}

viewProfile(){
    local uuid=$1
    if grep -qE "^$uuid:" "$userFile"; then
        grep -E "^$uuid:" "$userFile"
        return 0
    else
        echo "User not found"
        return 1
    fi
}
updateProfile(){
    local uuid=$2
    local username=$3
    local password=$4
    local role=$5
    local firstName=$6
    local lastName=$7
    local email=$8
    local dateofinfection=$9
    local onMedication=${10}
    local starDateofMedication=${11}
    local dob=${12}
    local country=${13}
    if grep -qE "^$uuid:" "$userFile"; then
        # sed/oldstring/newstring/g
        sed -i "s/^$uuid:.*/$uuid:$username:$password:$role:$firstName:$lastName:$email:$dateofinfection:$onMedication:$starDateofMedication:$dob:$country/" "$userFile"
        return 0
    else
        echo "User not found"
        return 1
    fi
}

exportUserData(){
    local patientfile="patientdata.csv"
    cat "$patientfile"
    // I want to aecho the headers to the text file
    echo "Username:Password:Role:UUID:FirstName:LastName:Email:DateOfInfection:OnMedication:StartDateOfMedication:DOB:Country" > "$patientfile"
    // I want to append data in user txt to patientdata.csv

    cat "$userFile" >> "$patientfile"
}

exportDataAnalytics(){
    local dataAnalyticsfile="dataanalytics.csv"
    touch "$dataAnalyticsfile"
}

getCountryLifeExpectancy(){
    local country=$2
    local lifeExpectancy=75
    local lifeExpectancyfile="life-expectancy.csv"
    lifeExpectancy=$(awk -F',' -v country="$country" '$1 == country {print $NF}' "$lifeExpectancyfile")
    echo "$lifeExpectancy"
}


functionName=$1
username=$2
password=$3
role=$4

case $functionName in
    createUser)
        createUser "$username" "$password"
        ;;
    loginUser)
        loginUser "$username" "$password"
        ;;
    checkUUID)
        checkUUID "$username"
        ;;
    completeRegistration)
        completeRegistration "$@"
        ;;
    viewProfile)
        viewProfile "$username"
        ;;
    updateProfile)
        updateProfile "$@"
        ;;
    exportUserData)
        exportUserData
        ;;
    exportDataAnalytics)
        exportDataAnalytics
        ;;
    getCountryLifeExpectancy)
        getCountryLifeExpectancy "$@"
        ;;
    *)
        echo "Invalid function name. Please try again."
        ;;
esac