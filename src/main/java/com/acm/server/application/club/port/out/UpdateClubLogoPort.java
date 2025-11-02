// com/acm/server/application/club/port/out/UpdateClubLogoPort.java
package com.acm.server.application.club.port.out;

public interface UpdateClubLogoPort {
    /** DB clubs.logo_url 만 갱신한다 */
    void updateClubLogo(Long clubId, String logoUrl);
}
