/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.server.managemnet;

import com.eac.db.entity.Server;

/**
 *
 * @author Sijin
 */
public interface VMControlAPI {

    public void start(Server s);

    public void stop(Server s);

}
