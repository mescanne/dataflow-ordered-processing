resource "google_bigquery_dataset" "demo_dataset" {
  dataset_id    = "ordered_processing_demo"
  friendly_name = "Ordered Processing Data"
  location      = var.bigquery_dataset_location
}

resource "google_bigquery_table" "market_depth" {
  deletion_protection = false
  dataset_id          = google_bigquery_dataset.demo_dataset.dataset_id
  table_id            = "market_depth"
  description         = "Market Depths"
  clustering          = ["contract_id"]

schema = <<EOF
[
  {
    "mode": "REQUIRED",
    "name": "contract_id",
    "type": "STRING"
  },
  {
    "mode": "REQUIRED",
    "name": "message_id",
    "type": "STRING"
  },
  {
    "mode": "REQUIRED",
    "name": "contract_sequence_id",
    "type": "STRING"
  },
  {
    "mode": "REQUIRED",
    "name": "bid_count",
    "type": "INTEGER"
  },
  {
    "mode": "REQUIRED",
    "name": "offer_count",
    "type": "INTEGER"
  },
  {
    "mode": "NULLABLE",
    "name": "last_trade",
    "type": "RECORD",
    "fields": [
            {
                "name": "price",
                "type": "INTEGER",
                "mode": "REQUIRED"
            },
            {
                "name": "quantity",
                "type": "INTEGER",
                "mode": "REQUIRED"
            }
    ]
  },
  {
    "mode": "REPEATED",
    "name": "bids",
    "type": "RECORD",
    "fields": [
            {
                "name": "price",
                "type": "INTEGER",
                "mode": "REQUIRED"
            },
            {
                "name": "quantity",
                "type": "INTEGER",
                "mode": "REQUIRED"
            }
    ]
  },
  {
    "mode": "REPEATED",
    "name": "offers",
    "type": "RECORD",
    "fields": [
            {
                "name": "price",
                "type": "INTEGER",
                "mode": "REQUIRED"
            },
            {
                "name": "quantity",
                "type": "INTEGER",
                "mode": "REQUIRED"
            }
    ]
  },
  {
    "mode": "REQUIRED",
    "name": "ingest_ts",
    "type": "TIMESTAMP",
    "defaultValueExpression": "CURRENT_TIMESTAMP()"
  }
]
EOF
}

resource "google_bigquery_table" "processing_status" {
deletion_protection = false
dataset_id = google_bigquery_dataset.demo_dataset.dataset_id
table_id = "processing_status"
description = "Ordered Processing Status"
clustering = ["contract_id"]

schema = <<EOF
[
  {
    "mode": "REQUIRED",
    "name": "contract_id",
    "type": "STRING"
  },
  {
    "mode": "REQUIRED",
    "name": "status_ts",
    "type": "TIMESTAMP"
  },
    {
    "mode": "REQUIRED",
    "name": "ingest_ts",
    "type": "TIMESTAMP",
    "defaultValueExpression": "CURRENT_TIMESTAMP()"
  },
  {
    "mode": "REQUIRED",
    "name": "received_count",
    "type": "INTEGER"
  },
  {
    "mode": "REQUIRED",
    "name": "buffered_count",
    "type": "INTEGER"
  },
  {
    "mode": "NULLABLE",
    "name": "last_processed_sequence",
    "type": "INTEGER"
  },
  {
    "mode": "NULLABLE",
    "name": "earliest_buffered_sequence",
    "type": "INTEGER"
  },
  {
    "mode": "NULLABLE",
    "name": "latest_buffered_sequence",
    "type": "INTEGER"
  }
]
EOF
}
