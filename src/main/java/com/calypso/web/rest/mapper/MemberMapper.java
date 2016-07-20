package com.calypso.web.rest.mapper;

import com.calypso.domain.*;
import com.calypso.web.rest.dto.MemberDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Member and its DTO MemberDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MemberMapper {

    @Mapping(source = "party.id", target = "partyId")
    @Mapping(source = "manager.id", target = "managerId")
    MemberDTO memberToMemberDTO(Member member);

    List<MemberDTO> membersToMemberDTOs(List<Member> members);

    @Mapping(source = "partyId", target = "party")
    @Mapping(source = "managerId", target = "manager")
    Member memberDTOToMember(MemberDTO memberDTO);

    List<Member> memberDTOsToMembers(List<MemberDTO> memberDTOs);

    default Party partyFromId(Long id) {
        if (id == null) {
            return null;
        }
        Party party = new Party();
        party.setId(id);
        return party;
    }

    default Member memberFromId(Long id) {
        if (id == null) {
            return null;
        }
        Member member = new Member();
        member.setId(id);
        return member;
    }
}
