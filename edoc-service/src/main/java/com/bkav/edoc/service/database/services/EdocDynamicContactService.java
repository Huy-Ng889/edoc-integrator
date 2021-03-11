package com.bkav.edoc.service.database.services;

import com.bkav.edoc.service.database.cache.OrganizationCacheEntry;
import com.bkav.edoc.service.database.daoimpl.EdocDynamicContactDaoImpl;
import com.bkav.edoc.service.database.entity.EdocDynamicContact;
import com.bkav.edoc.service.database.entity.pagination.PaginationCriteria;
import com.bkav.edoc.service.database.util.MapperUtil;
import com.bkav.edoc.service.memcached.MemcachedKey;
import com.bkav.edoc.service.memcached.MemcachedUtil;
import com.bkav.edoc.service.redis.RedisKey;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.util.*;

public class EdocDynamicContactService {
    private static final EdocDynamicContactDaoImpl dynamicContactDaoImpl = new EdocDynamicContactDaoImpl();

    public EdocDynamicContact getDynamicContactByDomain(String domain) {
        return dynamicContactDaoImpl.findByDomain(domain);
    }

    public List<EdocDynamicContact> getContactsByMultipleDomains(List<String> domains) {
        return dynamicContactDaoImpl.getContactByMultipleDomain(domains);
    }

    public String getNameByOrganId(String organId) {
        return dynamicContactDaoImpl.getNameByOrganId(organId);
    }

    public OrganizationCacheEntry getOrganizationCache(String domain) {
        String cacheKey = MemcachedKey.getKey(domain, MemcachedKey.DYNAMICCONTACT_KEY);

        OrganizationCacheEntry organizationCacheEntry = (OrganizationCacheEntry) MemcachedUtil.getInstance().read(cacheKey);

        if (organizationCacheEntry == null) {
            EdocDynamicContact dynamicContact = dynamicContactDaoImpl.findByDomain(domain);
            if (dynamicContact != null) {
                organizationCacheEntry = MapperUtil.modelToOrganCache(dynamicContact);
                MemcachedUtil.getInstance().create(cacheKey, MemcachedKey.GET_ORGAN_NAME_BY_DOMAIN_TIME, organizationCacheEntry);
            } else {
                organizationCacheEntry = new OrganizationCacheEntry();
                organizationCacheEntry.setStatus(true);
                organizationCacheEntry.setParent(0L);
                organizationCacheEntry.setToken("");
                organizationCacheEntry.setAddress("");
                organizationCacheEntry.setDomain(domain);
                organizationCacheEntry.setEmail(domain);
                organizationCacheEntry.setName(domain);
                organizationCacheEntry.setInCharge(domain);
                organizationCacheEntry.setTelephone(domain);
            }
        }
        return organizationCacheEntry;

    }

    public List<EdocDynamicContact> getAllDynamicContact() {
        return dynamicContactDaoImpl.findAll();
    }

    public EdocDynamicContact findEdocDynamicContactById(long contactId) {
        return dynamicContactDaoImpl.findById(contactId);
    }

    public void updateContact(EdocDynamicContact edocDynamicContact) {
        dynamicContactDaoImpl.updateContact(edocDynamicContact);
    }

    public void updateContact(OrganizationCacheEntry cacheEntry, EdocDynamicContact edocDynamicContact) {
        dynamicContactDaoImpl.updateContact(edocDynamicContact);
        List<OrganizationCacheEntry> organizationCacheEntries;
        String cacheKey = MemcachedKey.getKey("", RedisKey.GET_LIST_CONTACT_KEY);
        organizationCacheEntries = (List<OrganizationCacheEntry>) MemcachedUtil.getInstance().read(cacheKey);

        if (organizationCacheEntries == null) {
            organizationCacheEntries = new ArrayList<>();

            List<EdocDynamicContact> contacts = dynamicContactDaoImpl.findAll();

            for (EdocDynamicContact contact : contacts) {
                OrganizationCacheEntry organizationCacheEntry = MapperUtil.modelToOrganCache(contact);
                organizationCacheEntries.add(organizationCacheEntry);
            }
        } else {
            organizationCacheEntries.remove(cacheEntry);
            OrganizationCacheEntry newEntry = MapperUtil.modelToOrganCache(edocDynamicContact);
            organizationCacheEntries.add(newEntry);
        }
        MemcachedUtil.getInstance().create(cacheKey, MemcachedKey.CHECK_ALLOW_TIME_LIFE, organizationCacheEntries);
    }

    public OrganizationCacheEntry findById(long organId) {
        OrganizationCacheEntry organizationCacheEntry = null;
        EdocDynamicContact dynamicContact = dynamicContactDaoImpl.findById(organId);
        if (dynamicContact != null) {
            organizationCacheEntry = MapperUtil.modelToOrganCache(dynamicContact);
        }
        return organizationCacheEntry;
    }

    public boolean checkPermission(String organId, String token) {
        return dynamicContactDaoImpl.checkPermission(organId, token);
    }

    public EdocDynamicContact findContactByDomain(String organDomain) {
        return dynamicContactDaoImpl.findByDomain(organDomain);
    }

    public OrganizationCacheEntry findByDomain(String organDomain) {
        OrganizationCacheEntry organizationCacheEntry = null;
        EdocDynamicContact dynamicContact = dynamicContactDaoImpl.findByDomain(organDomain);
        if (dynamicContact != null) {
            organizationCacheEntry = MapperUtil.modelToOrganCache(dynamicContact);
        }
        return organizationCacheEntry;
    }

    public List<OrganizationCacheEntry> getAllContacts() {
        List<OrganizationCacheEntry> organizationCacheEntries;
        String cacheKey = MemcachedKey.getKey("", RedisKey.GET_LIST_CONTACT_KEY);
        MemcachedUtil.getInstance().delete(cacheKey);
        organizationCacheEntries = (List<OrganizationCacheEntry>) MemcachedUtil.getInstance().read(cacheKey);

        if (organizationCacheEntries == null) {
            organizationCacheEntries = new ArrayList<>();

            List<EdocDynamicContact> contacts = dynamicContactDaoImpl.findAll();

            for (EdocDynamicContact contact : contacts) {
                OrganizationCacheEntry organizationCacheEntry = MapperUtil.modelToOrganCache(contact);
                organizationCacheEntries.add(organizationCacheEntry);
            }

            MemcachedUtil.getInstance().create(cacheKey, MemcachedKey.CHECK_ALLOW_TIME_LIFE, organizationCacheEntries);
        }

        return organizationCacheEntries;
    }

    public Map<String, Object> getContacts(PaginationCriteria paginationCriteria) {
        int totalRecords = 0;
        List<OrganizationCacheEntry> contacts = new ArrayList<>();
        Session session = dynamicContactDaoImpl.openCurrentSession();
        Map<String, Object> map = null;
        try {
            StoredProcedureQuery storedProcedureQuery = session.createStoredProcedureQuery("GetOrganizations", EdocDynamicContact.class);
            storedProcedureQuery.registerStoredProcedureParameter("orderBy", String.class, ParameterMode.IN);
            storedProcedureQuery.registerStoredProcedureParameter("keyword", String.class, ParameterMode.IN);
            storedProcedureQuery.registerStoredProcedureParameter("pageIdx", Integer.class, ParameterMode.IN);
            storedProcedureQuery.registerStoredProcedureParameter("pageSize", Integer.class, ParameterMode.IN);
            storedProcedureQuery.registerStoredProcedureParameter("totalRecords", Integer.class, ParameterMode.OUT);
            storedProcedureQuery.setParameter("orderBy", paginationCriteria.getOrderBy());
            storedProcedureQuery.setParameter("keyword", paginationCriteria.getSearch());
            storedProcedureQuery.setParameter("pageIdx", paginationCriteria.getPageNumber());
            storedProcedureQuery.setParameter("pageSize", paginationCriteria.getPageSize());
            totalRecords = (Integer) storedProcedureQuery.getOutputParameterValue("totalRecords");

            List list = storedProcedureQuery.getResultList();

            if (list != null && list.size() > 0) {
                for (Object object : list) {
                    EdocDynamicContact contact = (EdocDynamicContact) object;
                    OrganizationCacheEntry cacheEntry = MapperUtil.modelToOrganCache(contact);
                    contacts.add(cacheEntry);
                }
                map = new HashMap<>();
                map.put("contacts", contacts);
                map.put("totalContacts", totalRecords);
                return map;
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Error get contacts cause " + e.getMessage());
            return map;
        } finally {
            dynamicContactDaoImpl.closeCurrentSession(session);
        }

    }

    public Long countOrgan(boolean agency) {

        return dynamicContactDaoImpl.countOrgan(agency);
    }

    public List<OrganizationCacheEntry> getDynamicContactsByAgency(boolean agency) {
        List<OrganizationCacheEntry> organizationCacheEntries;
        String cacheKey = MemcachedKey.getKey("", RedisKey.GET_LIST_CONTACT_BY_KEY);
        MemcachedUtil.getInstance().delete(cacheKey);
        organizationCacheEntries = (List<OrganizationCacheEntry>) MemcachedUtil.getInstance().read(cacheKey);

        if (organizationCacheEntries == null) {
            organizationCacheEntries = new ArrayList<>();
            List<EdocDynamicContact> contacts = dynamicContactDaoImpl.getDynamicContactsByAgency(agency);

            for (EdocDynamicContact contact : contacts) {
                OrganizationCacheEntry organizationCacheEntry = MapperUtil.modelToOrganCache(contact);
                organizationCacheEntries.add(organizationCacheEntry);
            }

            MemcachedUtil.getInstance().create(cacheKey, MemcachedKey.CHECK_ALLOW_TIME_LIFE, organizationCacheEntries);

        }

        return organizationCacheEntries;
    }

    public List<EdocDynamicContact> getDynamicContactByAgency(boolean agency) {
        return dynamicContactDaoImpl.getDynamicContactsByAgency(agency);
    }

    public void createContact(EdocDynamicContact contact) {
        dynamicContactDaoImpl.createContact(contact);
    }

    public boolean deleteOrgan(long organId) {
        EdocDynamicContact contactDel = dynamicContactDaoImpl.deleteOrgan(organId);
        boolean result = false;
        if (contactDel != null) {
            List<OrganizationCacheEntry> organizationCacheEntries;
            String cacheKey = MemcachedKey.getKey("", RedisKey.GET_LIST_CONTACT_KEY);
            MemcachedUtil.getInstance().delete(cacheKey);
            organizationCacheEntries = (List<OrganizationCacheEntry>) MemcachedUtil.getInstance().read(cacheKey);
            OrganizationCacheEntry organizationCacheEntryDel = MapperUtil.modelToOrganCache(contactDel);
            if (organizationCacheEntries == null) {
                organizationCacheEntries = new ArrayList<>();

                List<EdocDynamicContact> contacts = dynamicContactDaoImpl.findAll();

                for (EdocDynamicContact contact : contacts) {
                    OrganizationCacheEntry organizationCacheEntry = MapperUtil.modelToOrganCache(contact);
                    organizationCacheEntries.add(organizationCacheEntry);
                }
            } else {
                organizationCacheEntries.remove(organizationCacheEntryDel);
            }
            MemcachedUtil.getInstance().create(cacheKey, MemcachedKey.CHECK_ALLOW_TIME_LIFE, organizationCacheEntries);
            result = true;
        }
        return result;
    }

    public List<String> getAllDomain() {
        return dynamicContactDaoImpl.getAllDomain();
    }

    public List<EdocDynamicContact> getAllChildOrgan (String parentDomain) {
        List<EdocDynamicContact> childOrgans;
        int index = 0;

        String[] parentOrganSplit = parentDomain.split("\\.");
        if (parentOrganSplit[0].equals("000")) {
            if (parentOrganSplit[1].equals("00")) {
                if (parentOrganSplit[2].equals("00")) {
                    index = 3;
                } else
                    index = 2;
            } else
                index = 1;
        } else {
            return null;
        }

        String regexParent = "";
        for (int i = index; i < parentOrganSplit.length; i++) {
            regexParent += parentOrganSplit[i];
            if (i == 3)
                break;
            regexParent += ".";
        }
        System.out.println(regexParent);
        childOrgans = dynamicContactDaoImpl.getAllChildrenContact(regexParent);
        return childOrgans;
    }

    public static void main(String[] args) {
        String parent = "000.00.33.A53";
        EdocDynamicContactService edocDynamicContactService = new EdocDynamicContactService();
        List<EdocDynamicContact> childrent =  edocDynamicContactService.getAllChildOrgan(parent);
        if (childrent != null) {
            System.out.println("List childrent: ");
            for (EdocDynamicContact contact: childrent) {
                if (!contact.getDomain().equals(parent)) {
                    System.out.println(contact.getDomain());
                }
            }
        } else {
            System.out.println("No child !!!!!");
        }
    }

    private final static Logger LOGGER = Logger.getLogger(EdocDynamicContactService.class);
}
