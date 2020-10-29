import json
import base64
import boto3

def lambda_handler(event, context):
    s3 = boto3.client("s3")
    
    # For fetching bucket_name & file_name using proxy integration method from API Gateway
    bucket_name = event["pathParameters"]["bucket"]
    #file_name = event["queryStringParameters"]["file"]
    fileURLs = []
    
    for key in s3.list_objects(Bucket=bucket_name)['Contents']:
        print(key['Key'])
        fileURLs.insert(len(fileURLs), s3.generate_presigned_url("get_object", Params = {"Bucket": bucket_name, "Key": key['Key']},  ExpiresIn=3600))
    
    return {
        "statusCode": 200,
        "headers": {
            "Content-Type": "application/json"
        },
        "body": json.dumps({"URLs": fileURLs})
        }