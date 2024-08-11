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
    local userFile="user.txt"  # Assuming userFile is defined somewhere in your script
    echo "Exporting user data..."
    # Echo the headers to the text file
    echo "UUID,Username,Password,Role,FirstName,LastName,Email,DateOfInfection,OnMedication,StartDateOfMedication,DOB,Country" > "$patientfile"

    # Read each line from userFile
    while IFS= read -r line; do
        # Extract each field one by one
        uuid=$(echo "$line" | cut -d':' -f1)
        username=$(echo "$line" | cut -d':' -f2)
        password=$(echo "$line" | cut -d':' -f3)
        role=$(echo "$line" | cut -d':' -f4)
        firstName=$(echo "$line" | cut -d':' -f5)
        lastName=$(echo "$line" | cut -d':' -f6)
        email=$(echo "$line" | cut -d':' -f7)
        dateofinfection=$(echo "$line" | cut -d':' -f8-10)  # Extract dateofinfection with two colons
        onMedication=$(echo "$line" | cut -d':' -f11)
        starDateofMedication=$(echo "$line" | cut -d':' -f12-14)
        dob=$(echo "$line" | cut -d':' -f15-17)  # Extract dob with two colons
        country=$(echo "$line" | cut -d':' -f18)
        # Reconstruct the line with commas, preserving the timezone field
        echo "$uuid,$username,$password,$role,$firstName,$lastName,$email,$dateofinfection,$onMedication,$starDateofMedication,$dob,$country" >> "$patientfile"
    done < "$userFile"
}
exportDataAnalytics(){
    local dataAnalyticsfile="dataanalytics.csv"
    local patientsdataFile="patientdata.csv"
    local lpmtAverage=$2
    local lpmtMedian=$3
    local percintile25=$4
    local percintile50=$5
    local percintile75=$6

    echo "LPMT Average,LPMT Median,25th Percentile,50th Percentile,75th Percentile" > "$dataAnalyticsfile"
    echo "$lpmtAverage,$lpmtMedian,$percintile25,$percintile50,$percintile75" >> "$dataAnalyticsfile"

    
    # Calculate the average life expectancy of patients
    # Calculate the average number of patients per country
    # Calculate the average number of patients on medication
    # Calculate the average number of patients not on medication

}

getUsersData(){
    local userFile="user.txt"
    cat "$userFile"
}

getCountryLifeExpectancy(){
    local country=$2
    local lifeExpectancy=75
    local lifeExpectancyfile="life-expectancy.csv"
    lifeExpectancy=$(awk -F',' -v country="$country" '$1 == country {print $NF}' "$lifeExpectancyfile")
    echo "$lifeExpectancy"
}
# exportDataAnalytics

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
        exportUserData "$@"
        ;;
    exportDataAnalytics)
        exportDataAnalytics "$@"
        ;;
    getCountryLifeExpectancy)
        getCountryLifeExpectancy "$@"
        ;;
    getUsersData)
        getUsersData
        ;;

    *)
        echo "Invalid function name. Please try again."
        ;;
esac
