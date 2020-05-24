resource "aws_dynamodb_table" "io-nbugash-titan-captures" {
  hash_key = "id"
  name = "io.nbugash.titan.captures"
  read_capacity = 5
  write_capacity = 5
  attribute {
    name = "id"
    type = "N"
  }
  tags = {
    Environment = "POC"
  }
}
