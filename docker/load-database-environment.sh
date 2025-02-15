# Script to load environment variables to run docker-compose

eval $(python3 -c "
import yaml
with open('../database/database.yml') as file:
    config = yaml.safe_load(file)
    database = config['database']
    print(f'export DB_URL={database[\"url\"]}')
    print(f'export DB_USERNAME={database[\"username\"]}')
    print(f'export DB_PASSWORD={database[\"password\"]}')
")

#echo "Database environment variables loaded successfully!"
#echo "DB_URL=$DB_URL"
#echo "DB_USERNAME=$DB_USERNAME"
#echo "DB_PASSWORD=$DB_PASSWORD"

# To execute:
# shell: bash ./load-database-environment.sh && docker-compose up -d
# zsh: source ./load-database-environment.sh && docker-compose up -d