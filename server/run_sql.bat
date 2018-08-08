SETX SQLALCHEMY_DATABASE_URI "mysql+pymysql://backend:FGXq1eNDJCrOoEKUjSUq2@127.0.0.1:3307/operations"
SETX PEEWEE_DATABASE_URI "mysql://backend:FGXq1eNDJCrOoEKUjSUq2@127.0.0.1:3307/operations"

.\resources\cloud_sql_proxy.exe -instances=eps-scams:us-east1:eps-scams-01=tcp:3307 -credential_file=credentials.json