#!/usr/bin/env bash
for data in ~/bigdata/year/*.gz
do
	gunzip -c $data | \
		awk '{ temp = substr($0, 88, 5) + 0;
	q = substr($0, 93, 1);
	if(temp!=9999&&q~/[01459]/&&temp>max) {
		max=temp;
		date = substr($0, 16, 8);
	}
	}
		END{print date; print max;}'
	done
