resource "aws_kinesis_stream" "titan-dynamodb-stream" {
  name = "dynamodb"
  shard_count = 1
}
resource "aws_kinesis_stream" "titan-s3-stream" {
  name = "s3"
  shard_count = 1
}
resource "aws_kinesis_stream" "titan-redshift-stream" {
  name = "redshift"
  shard_count = 1
}
