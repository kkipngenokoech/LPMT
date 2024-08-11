userFile="generateduser.txt"
delimiter=":"

# I want to find the lengths of each line in the file
echo "File: $userFile"
while IFS= read -r line; do
    echo "Original Line: $line"
    IFS="$delimiter" read -ra parts <<< "$line"
    for part in "${parts[@]}"; do
        echo "Part: $part, Length: ${#part}"
    done
done < "$userFile"