package home.work.finance;

import home.work.finance.model.User;
import home.work.finance.model.Wallet;
import home.work.finance.repository.WalletRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WalletRepositoryTests {
    private static final WalletRepository repository = new WalletRepository();
    @BeforeAll
    static void init() {

    }

    @Test
    void loadWalletsByUser() {
        assertEquals(0, repository.loadWalletsByUser("test1").size());
    }

    @Test
    void loadWalletsByNameAndUser() {
        User user = new User("wallet_load", "");
        String wallet = "wallet_finances";
        repository.saveWallet(new Wallet(user.getUsername(), wallet, 100));
        assertEquals(wallet, repository.loadWalletsByNameAndUser(wallet, user.getUsername()).getName());
        repository.saveWallets(repository.loadWallets().stream().filter(w -> w.getUserId().equals(user.getUsername()) && w.getName().equals(wallet)).toList());
    }
}
