/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eac.container.algorithm.cluster;

import com.eac.db.entity.Cluster;
import java.util.List;

/**
 *
 * @author Sijin
 */
public interface ClusterAlgorithm {

    public Cluster run(List<Cluster> clusterList);
}
