resource "aws_s3_bucket" "titan-s3-bucket" {
  bucket = "titan-s3-bucket"
  force_destroy = true
  object_lock_configuration {
    object_lock_enabled = "Enabled"
  }
}
