package com.bkav.edoc.service.database.daoimpl;

import com.bkav.edoc.service.database.dao.EdocDynamicContactDao;
import com.bkav.edoc.service.database.entity.EdocDynamicContact;
import com.bkav.edoc.service.kernel.string.StringPool;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EdocDynamicContactDaoImpl extends RootDaoImpl<EdocDynamicContact, Long> implements EdocDynamicContactDao {

    public EdocDynamicContactDaoImpl() {
        super(EdocDynamicContact.class);
    }

    public EdocDynamicContact findByDomain(String domain) {
        Session currentSession = openCurrentSession();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT edc FROM EdocDynamicContact edc where edc.domain=:domain");
            Query<EdocDynamicContact> query = currentSession.createQuery(sql.toString(), EdocDynamicContact.class);
            query.setParameter("domain", domain);
            List<EdocDynamicContact> result = query.list();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
            LOGGER.warn("Not found dynamic contact for organ domain " + domain);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error get dynamic contact from organ domain " + domain + " cause " + e.getMessage());
            return null;
        } finally {
            currentSession.close();
        }
    }

    @Override
    public List<EdocDynamicContact> getDynamicContactsByDomainFilter(String domain) {
        Session currentSession = openCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT edc FROM EdocDynamicContact edc where edc.domain like :domain");
        Query<EdocDynamicContact> query = currentSession.createQuery(sql.toString(), EdocDynamicContact.class);
        query.setParameter("domain", StringPool.PERCENT + domain + StringPool.PERCENT);
        closeCurrentSession(currentSession);
        return query.list();
    }

    @Override
    public Long countOrgan(String organDomain) {
        Session currentSession = openCurrentSession();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(*) FROM EdocDynamicContact edc where edc.domain like :domain");
        Query<Long> query = currentSession.createQuery(sql.toString(), Long.class);
        query.setParameter("domain", StringPool.PERCENT + organDomain + StringPool.PERCENT);
        closeCurrentSession(currentSession);
        return query.uniqueResult();
    }

    @Override
    public boolean checkPermission(String organId, String token) {
        boolean result = false;
        Session session = openCurrentSession();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT edc from EdocDynamicContact edc where edc.domain=:domain and token=:token and edc.status=true");
            Query<EdocDynamicContact> query = session.createQuery(sql.toString(), EdocDynamicContact.class);
            query.setParameter("domain", organId);
            query.setParameter("token", token);
            List<EdocDynamicContact> dynamicContacts = query.list();
            if (dynamicContacts != null && dynamicContacts.size() > 0) {
                result = true;
            }
            LOGGER.info("Check permission success for organ " + organId);
        } catch (Exception e) {
            LOGGER.error("Error when check permission for organId " + organId + " cause " + e.getMessage());
        } finally {
            closeCurrentSession(session);
        }
        return result;
    }

    @Override
    public void updateContact(EdocDynamicContact edocDynamicContact) {
        saveOrUpdate(edocDynamicContact);
    }

    @Override
    public void createContact(EdocDynamicContact contact) {
        this.persist(contact);
    }

    @Override
    public EdocDynamicContact deleteOrgan(long organId) {
        Session session = openCurrentSession();
        EdocDynamicContact contact = null;
        try {
            session.beginTransaction();
            EdocDynamicContact organ = this.findById(organId);
            if (organ == null) {
                LOGGER.error("Error delete organ not found document with id " + organId);
            } else {
                session.delete(organ);
                session.getTransaction().commit();
                contact = organ;
            }
        } catch (Exception e) {
            LOGGER.error("Error delete organ with id " + organId + " cause " + e.getMessage());
            session.getTransaction().rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return contact;
    }

    private static final Logger LOGGER = Logger.getLogger(EdocDynamicContactDaoImpl.class);
}
