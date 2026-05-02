package com.localgaji.taxi.address.type;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoadNameParser {
    public static RoadNameAddress parse(String address) {
        // 패턴 정리:
        // 1. (시/도 시/군)
        // 2. (구/읍/면) (optional)
        // 3. (도로명)
        // 4. (건물번호)
        String regex = "^" +
                "([가-힣]+(?:도|특별자치시|특별시|광역시)?\\s[가-힣]+(?:시|군))\\s" +  // city
                "(?:([가-힣]+(?:구|읍|면))\\s)?" +                                     // district (optional)
                "([가-힣0-9]+(?:로|길|대로|번길|로\\d+번길|로\\d+길))\\s" +                     // roadName
                "([0-9]+(?:-[0-9]+)?)" +                                              // buildingNumber
                "$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address.trim());

        if (matcher.matches()) {
            String city = matcher.group(1);
            String district = matcher.group(2) == null ? "" : matcher.group(2);
            String roadName = matcher.group(3);
            String buildingNumber = matcher.group(4);
            return RoadNameAddress.builder()
                    .city(city)
                    .district(district)
                    .roadName(roadName)
                    .buildingNumber(buildingNumber)
                    .build();
        } else {
            throw new CustomException(ErrorType.INVALID_ADDRESS);
        }
    }
}
