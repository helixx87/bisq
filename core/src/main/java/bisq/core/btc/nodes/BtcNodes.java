/*
 * This file is part of Bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.btc.nodes;

import bisq.common.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

// Managed here: https://github.com/bisq-network/roles/issues/39
@Slf4j
public class BtcNodes {

    public enum BitcoinNodesOption {
        PROVIDED,
        CUSTOM,
        PUBLIC
    }

    // For other base currencies or testnet we ignore provided nodes
    public List<BtcNode> getProvidedBtcNodes() {
        return useProvidedBtcNodes() ?
                Arrays.asList(
                        // emzy
                        new BtcNode("btcnode1.emzy.de", "emzybtc3ewh7zihpkdvuwlgxrhzcxy2p5fvjggp7ngjbxcytxvt4rjid.onion", "167.86.90.239", BtcNode.DEFAULT_PORT, "@emzy"),
                        // emzy unstable
                        new BtcNode("btcnode2.emzy.de", "emzybtc25oddoa2prol2znpz2axnrg6k77xwgirmhv7igoiucddsxiad.onion", "62.171.129.32", BtcNode.DEFAULT_PORT, "@emzy (unstable)"),
                        new BtcNode("btcnode4.emzy.de", "emzybtc454ewbviqnmgtgx3rgublsgkk23r4onbhidcv36wremue4kqd.onion", "135.181.215.237", BtcNode.DEFAULT_PORT, "@emzy (very unstable)"),

                        // mrosseel unstable
                        new BtcNode("btc.vante.me", "bsqbtctulf2g4jtjsdfgl2ed7qs6zz5wqx27qnyiik7laockryvszqqd.onion", "94.23.21.80", BtcNode.DEFAULT_PORT, "@miker (unstable)"),

                        // sqrrm
                        new BtcNode("btc1.sqrrm.net", "cwi3ekrwhig47dhhzfenr5hbvckj7fzaojygvazi2lucsenwbzwoyiqd.onion", "185.25.48.184", BtcNode.DEFAULT_PORT, "@sqrrm"),
                        // sqrrm unstable
                        new BtcNode("btc2.sqrrm.net", "upvthy74hgvgbqi6w3zd2mlchoi5tvvw7b5hpmmhcddd5fnnwrixneid.onion", "81.171.22.143", BtcNode.DEFAULT_PORT, "@sqrrm (unstable)"),

                        // Devin Bileck
                        new BtcNode("btc1.bisq.services", "devinbtctu7uctl7hly2juu3thbgeivfnvw3ckj3phy6nyvpnx66yeyd.onion", "172.105.21.216", BtcNode.DEFAULT_PORT, "@devinbileck"),
                        new BtcNode(null, "devinbtcmwkuitvxl3tfi5of4zau46ymeannkjv6fpnylkgf3q5fa3id.onion", null, BtcNode.DEFAULT_PORT, "@devinbileck"),

                        // wiz
                        new BtcNode("node210.fmt.wiz.biz", "rfqmn3qe36uaptkxhdvi74p4hyrzhir6vhmzb2hqryxodig4gue2zbyd.onion", "103.99.170.210", BtcNode.DEFAULT_PORT, "@wiz"),
                        // wiz unstable
                        new BtcNode("node220.fmt.wiz.biz", "azbpsh4arqlm6442wfimy7qr65bmha2zhgjg7wbaji6vvaug53hur2qd.onion", "103.99.170.220", BtcNode.DEFAULT_PORT, "@wiz (very unstable)"),

                        // jester4042
                        new BtcNode(null, "nhpftqp3kmcnksw2ev6tkwq47jhy37movbjimnd577jcrtwmadtirrqd.onion", null, BtcNode.DEFAULT_PORT, "@jester4042"),

                        // node_op_324
                        new BtcNode(null, "qs535l32ne43rxr5iqexbhu4r6zifrfez653pm7j3rpi7c7omaz7xcqd.onion", null, BtcNode.DEFAULT_PORT, "@node_op_324"),

                        // btcNodl
                        new BtcNode(null, "2oalsctcn76axnrnaqjddiiu5qhrc7hv3raik2lyfxb7eoktk4vw6sad.onion", null, BtcNode.DEFAULT_PORT, "@btcNodl"),

                        // runbtc
                        new BtcNode(null, "runbtcnd22qxdwlmhzsrw6zyfmkivuy5nuqbhasaztekildcxc7lseyd.onion", null, BtcNode.DEFAULT_PORT, "@runbtc")
                ) :
                new ArrayList<>();
    }

    public boolean useProvidedBtcNodes() {
        return Config.baseCurrencyNetwork().isMainnet();
    }

    public static List<BtcNodes.BtcNode> toBtcNodesList(Collection<String> nodes) {
        return nodes.stream()
                .filter(e -> !e.isEmpty())
                .map(BtcNodes.BtcNode::fromFullAddress)
                .collect(Collectors.toList());
    }

    @EqualsAndHashCode
    @Getter
    public static class BtcNode {
        private static final int DEFAULT_PORT = Config.baseCurrencyNetworkParameters().getPort(); //8333

        @Nullable
        private final String onionAddress;
        @Nullable
        private final String hostName;
        @Nullable
        private final String operator; // null in case the user provides a list of custom btc nodes
        @Nullable
        private final String address; // IPv4 address
        private int port = DEFAULT_PORT;

        /**
         * @param fullAddress [IPv4 address:port or onion:port]
         * @return BtcNode instance
         */
        public static BtcNode fromFullAddress(String fullAddress) {
            String[] parts = fullAddress.split("]");
            checkArgument(parts.length > 0);
            String host = "";
            int port = DEFAULT_PORT;
            if (parts[0].contains("[") && parts[0].contains(":")) {
                // IPv6 address and optional port number
                // address part delimited by square brackets e.g. [2a01:123:456:789::2]:8333
                host = parts[0].replace("[", "").replace("]", "");
                if (parts.length == 2)
                    port = Integer.parseInt(parts[1].replace(":", ""));
            } else if (parts[0].contains(":") && !parts[0].contains(".")) {
                // IPv6 address only; not delimited by square brackets
                host = parts[0];
            } else if (parts[0].contains(".")) {
                // address and an optional port number
                // e.g. 127.0.0.1:8333 or abcdef123xyz.onion:9999
                parts = fullAddress.split(":");
                checkArgument(parts.length > 0);
                host = parts[0];
                if (parts.length == 2)
                    port = Integer.parseInt(parts[1]);
            }

            checkArgument(host.length() > 0, "BtcNode address format not recognised");
            return host.contains(".onion") ? new BtcNode(null, host, null, port, null) : new BtcNode(null, null, host, port, null);
        }

        public BtcNode(@Nullable String hostName,
                       @Nullable String onionAddress,
                       @Nullable String address,
                       int port,
                       @Nullable String operator) {
            this.hostName = hostName;
            this.onionAddress = onionAddress;
            this.address = address;
            this.port = port;
            this.operator = operator;
        }

        public boolean hasOnionAddress() {
            return onionAddress != null;
        }

        public String getHostNameOrAddress() {
            if (hostName != null)
                return hostName;
            else
                return address;
        }

        public boolean hasClearNetAddress() {
            return hostName != null || address != null;
        }

        @Override
        public String toString() {
            return "onionAddress='" + onionAddress + '\'' +
                    ", hostName='" + hostName + '\'' +
                    ", address='" + address + '\'' +
                    ", port='" + port + '\'' +
                    ", operator='" + operator;
        }

        public String getId() {
            String address = this.address == null ? "" : this.address + ", ";
            String onionAddress = this.onionAddress == null ? "" : this.onionAddress;
            return operator + ": [" + address + onionAddress + "]";
        }
    }
}
